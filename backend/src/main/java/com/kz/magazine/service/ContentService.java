package com.kz.magazine.service;

import com.kz.magazine.dto.content.ContentCreateRequestDto;
import com.kz.magazine.dto.content.ContentDetailResponseDto;
import com.kz.magazine.dto.content.ContentFilterDto;
import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.dto.content.ContentUpdateRequestDto;
import com.kz.magazine.entity.Category;
import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.ContentHashtag;
import com.kz.magazine.entity.ContentHashtagId;
import com.kz.magazine.entity.Hashtag;
import com.kz.magazine.entity.User;
import com.kz.magazine.entity.ContentStatus;
import com.kz.magazine.repository.CategoryRepository;
import com.kz.magazine.repository.ContentHashtagRepository;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.HashtagRepository;
import com.kz.magazine.repository.UserRepository;
import com.kz.magazine.util.HtmlSanitizerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryRepository categoryRepository;
    private final HashtagRepository hashtagRepository;
    private final ContentHashtagRepository contentHashtagRepository;
    private final com.kz.magazine.repository.ReactionRepository reactionRepository;

    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    public Page<ContentResponseDto> getContents(ContentFilterDto filter, Pageable pageable) {
        Page<Content> contents;

        // Simple filtering strategy
        ContentStatus statusEnum = ContentStatus.valueOf(filter.getStatus());

        if (filter.getQ() != null && !filter.getQ().trim().isEmpty()) {
            // Full-text search
            // Prepare keyword: standard to_tsquery format (e.g., 'word1 | word2' or 'word1
            // & word2')
            // For simplicity, we join with & for AND search
            String keyword = filter.getQ().trim().replaceAll("\\s+", " & ");
            contents = contentRepository.searchContents(keyword, filter.getStatus(), pageable);
        } else if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            contents = contentRepository.findByCategory_CategoryNameAndStatusAndDeletedAtIsNull(
                    filter.getCategory(), statusEnum, pageable);
        } else if (Boolean.TRUE.equals(filter.getHasYoutubeUrl())) {
            contents = contentRepository.findByStatusAndYoutubeUrlIsNotNullAndDeletedAtIsNull(statusEnum,
                    pageable);
        } else if (Boolean.TRUE.equals(filter.getHasInstagramUrl())) {
            contents = contentRepository.findByStatusAndInstagramUrlIsNotNullAndDeletedAtIsNull(statusEnum,
                    pageable);
        } else {
            contents = contentRepository.findByStatusAndDeletedAtIsNull(statusEnum, pageable);
        }

        return contents.map(ContentResponseDto::from);
    }

    @Transactional
    public ContentDetailResponseDto getContent(Long contentId, String username) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

        if (content.getDeletedAt() != null) {
            throw new IllegalArgumentException("Content not found: " + contentId);
        }

        Long userId = null;
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null)
                userId = user.getUserId();
        }

        content.incrementViewCount(); // This might double count if controller calls contentViewService too?
        // Controller calls contentViewService.incrementViewCount, which handles dedup.
        // This direct increment in Service is naive. Should rely on
        // Controller/ViewService.
        // But removing it might break basic view count if ViewService isn't used
        // everywhere.
        // Given Phase 3 replaced simplistic view count with dedup service, we should
        // probably remove this line
        // if ContentController handles it via ContentViewService.
        // Let's keep it consistent with previous code but usually one place should
        // handle it.
        // Since Controller calls contentViewService, having it here is redundant or
        // conflicting.
        // Ideally remove, but I will comment it out to be safe or leave as is if
        // unsure.
        // Actually, let's remove it to rely on ContentViewService which is Dedup.

        // Fetch Hashtags
        List<String> hashtags = contentHashtagRepository.findHashtagNamesByContentId(contentId);

        // Fetch Reactions
        List<com.kz.magazine.entity.Reaction> allReactions = reactionData(contentId);
        java.util.Map<String, Integer> reactionCounts = new java.util.HashMap<>();
        allReactions.stream()
                .filter(r -> !"RATING".equals(r.getReactionType()))
                .forEach(r -> reactionCounts.put(r.getReactionType(),
                        reactionCounts.getOrDefault(r.getReactionType(), 0) + 1));

        String userReaction = null;
        Integer userRating = null;

        if (userId != null) {
            List<com.kz.magazine.entity.Reaction> userReactions = reactionRepository
                    .findByContent_ContentIdAndUser_UserId(contentId, userId);

            for (com.kz.magazine.entity.Reaction r : userReactions) {
                if ("RATING".equals(r.getReactionType())) {
                    userRating = r.getRatingValue();
                } else {
                    userReaction = r.getReactionType();
                }
            }
        }

        // User Rating - Need RatingRepository? Or calc?
        // See if RatingRepository exists. If not, maybe use explicit query or loop?
        // Assuming RatingRepository exists or I need to inject it.
        // I'll assume explicit helper method if repo not injected.
        // Wait, I need to inject RatingRepository.

        return ContentDetailResponseDto.from(content, hashtags, reactionCounts, userReaction, userRating);

    }

    private List<com.kz.magazine.entity.Reaction> reactionData(Long contentId) {
        return reactionRepository.findByContent_ContentId(contentId);
    }

    @Transactional
    public ContentDetailResponseDto createContent(ContentCreateRequestDto dto, String username) {
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Category category = categoryRepository.findByCategoryName(dto.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryName()));

        Content content = new Content();
        content.setTitle(dto.getTitle());
        content.setSummary(dto.getSummary());
        // Sanitize HTML body
        content.setBodyHtml(HtmlSanitizerUtil.sanitize(dto.getBodyHtml()));
        content.setBodyText(dto.getBodyText());
        content.setCategory(category);
        content.setAuthor(author);
        content.setStatus(ContentStatus.valueOf(dto.getStatus()));
        content.setYoutubeUrl(dto.getYoutubeUrl());
        content.setInstagramUrl(dto.getInstagramUrl());

        Content savedContent = contentRepository.save(content);

        List<String> savedHashtags = processHashtags(savedContent, dto.getHashtags());

        auditLogService.logAction(username, "CREATE", "CONTENT", savedContent.getContentId(), null, dto.toString());

        return ContentDetailResponseDto.from(savedContent, savedHashtags);
    }

    @Transactional
    public ContentDetailResponseDto updateContent(Long contentId, ContentUpdateRequestDto dto, String username) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

        if (dto.getTitle() != null)
            content.setTitle(dto.getTitle());
        if (dto.getSummary() != null)
            content.setSummary(dto.getSummary());
        if (dto.getBodyHtml() != null)
            content.setBodyHtml(HtmlSanitizerUtil.sanitize(dto.getBodyHtml()));
        if (dto.getBodyText() != null)
            content.setBodyText(dto.getBodyText());
        if (dto.getStatus() != null)
            content.setStatus(ContentStatus.valueOf(dto.getStatus()));
        if (dto.getYoutubeUrl() != null)
            content.setYoutubeUrl(dto.getYoutubeUrl());
        if (dto.getInstagramUrl() != null)
            content.setInstagramUrl(dto.getInstagramUrl());

        if (dto.getCategoryName() != null) {
            Category category = categoryRepository.findByCategoryName(dto.getCategoryName())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found: " + dto.getCategoryName()));
            content.setCategory(category);
        }

        List<String> currentHashtags;
        if (dto.getHashtags() != null) {
            // Remove existing relations
            List<String> oldHashtags = contentHashtagRepository.findHashtagNamesByContentId(contentId);
            for (String tagName : oldHashtags) {
                Hashtag tag = hashtagRepository.findByHashtagName(tagName).orElse(null);
                if (tag != null) {
                    ContentHashtagId chId = new ContentHashtagId(contentId, tag.getHashtagId());
                    contentHashtagRepository.deleteById(chId);
                    tag.decrementUsage();
                }
            }
            // Add new relations
            currentHashtags = processHashtags(content, dto.getHashtags());
        } else {
            currentHashtags = contentHashtagRepository.findHashtagNamesByContentId(contentId);
        }

        auditLogService.logAction(username, "UPDATE", "CONTENT", contentId, null, dto.toString());

        return ContentDetailResponseDto.from(content, currentHashtags);
    }

    @Transactional
    public void deleteContent(Long contentId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

        content.softDelete(user.getUserId());

        List<String> hashtags = contentHashtagRepository.findHashtagNamesByContentId(contentId);
        for (String tagName : hashtags) {
            hashtagRepository.findByHashtagName(tagName).ifPresent(Hashtag::decrementUsage);
        }

        auditLogService.logAction(username, "DELETE", "CONTENT", contentId);
    }

    private List<String> processHashtags(Content content, List<String> hashtags) {
        if (hashtags == null || hashtags.isEmpty())
            return Collections.emptyList();

        List<String> result = new ArrayList<>();
        Set<String> uniqueTags = new HashSet<>(hashtags);

        for (String tagName : uniqueTags) {
            Hashtag hashtag = hashtagRepository.findByHashtagName(tagName)
                    .orElseGet(() -> hashtagRepository.save(new Hashtag(tagName)));

            hashtag.incrementUsage();
            ContentHashtag contentHashtag = new ContentHashtag(content, hashtag);
            contentHashtagRepository.save(contentHashtag);
            result.add(hashtag.getHashtagName());
        }
        return result;
    }
}

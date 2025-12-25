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
    private final UserRepository userRepository;

    public Page<ContentResponseDto> getContents(ContentFilterDto filter, Pageable pageable) {
        Page<Content> contents;

        // Simple filtering strategy
        if (filter.getCategory() != null && !filter.getCategory().isEmpty()) {
            contents = contentRepository.findByCategory_CategoryNameAndStatusAndDeletedAtIsNull(
                    filter.getCategory(), filter.getStatus(), pageable);
        } else {
            contents = contentRepository.findByStatusAndDeletedAtIsNull(filter.getStatus(), pageable);
        }

        return contents.map(ContentResponseDto::from);
    }

    @Transactional
    public ContentDetailResponseDto getContent(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found: " + contentId));

        if (content.getDeletedAt() != null) {
            throw new IllegalArgumentException("Content not found: " + contentId);
        }

        content.incrementViewCount();

        List<String> hashtags = contentHashtagRepository.findHashtagNamesByContentId(contentId);

        return ContentDetailResponseDto.from(content, hashtags);
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
        content.setStatus(dto.getStatus());

        Content savedContent = contentRepository.save(content);

        List<String> savedHashtags = processHashtags(savedContent, dto.getHashtags());

        return ContentDetailResponseDto.from(savedContent, savedHashtags);
    }

    @Transactional
    public ContentDetailResponseDto updateContent(Long contentId, ContentUpdateRequestDto dto) {
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
            content.setStatus(dto.getStatus());

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

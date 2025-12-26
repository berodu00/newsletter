package com.kz.magazine.service;

import com.kz.magazine.dto.idea.IdeaRequestDto;
import com.kz.magazine.dto.idea.IdeaResponseDto;
import com.kz.magazine.dto.idea.IdeaReviewDto;
import com.kz.magazine.entity.Idea;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.IdeaRepository;
import com.kz.magazine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final UserRepository userRepository;

    public Page<IdeaResponseDto> getIdeas(String username, boolean isAdmin, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (isAdmin) {
            return ideaRepository.findByDeletedAtIsNull(pageable)
                    .map(IdeaResponseDto::from);
        } else {
            return ideaRepository.findByUser_UserIdAndDeletedAtIsNull(user.getUserId(), pageable)
                    .map(IdeaResponseDto::from);
        }
    }

    @Transactional
    public IdeaResponseDto createIdea(String username, IdeaRequestDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Idea idea = Idea.builder()
                .user(user)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();

        return IdeaResponseDto.from(ideaRepository.save(idea));
    }

    @Transactional
    public IdeaResponseDto reviewIdea(Long ideaId, String username, IdeaReviewDto dto) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found"));

        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new IllegalArgumentException("Idea not found"));

        idea.setStatus(dto.getStatus());
        idea.setAdminComment(dto.getAdminComment());
        idea.setReviewedBy(admin);
        idea.setReviewedAt(java.time.LocalDateTime.now());

        return IdeaResponseDto.from(idea);
    }
}

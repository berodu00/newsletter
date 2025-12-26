package com.kz.magazine.service;

import com.kz.magazine.dto.content.ContentDetailResponseDto;
import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.entity.Content;
import com.kz.magazine.entity.ContentStatus;
import com.kz.magazine.entity.Category;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.ContentRepository;
import com.kz.magazine.repository.ReactionRepository;
import com.kz.magazine.repository.UserRepository;
import com.kz.magazine.repository.ContentHashtagRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

        @InjectMocks
        private ContentService contentService;

        @Mock
        private ContentRepository contentRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private ReactionRepository reactionRepository;

        @Mock
        private ContentHashtagRepository contentHashtagRepository;

        @Test
        @DisplayName("사보 목록 조회 성공")
        void getContents_Success() {
                // given
                Category category = Category.builder().categoryName("General").build();
                User author = User.builder().name("Author").build();

                Content content = Content.builder()
                                .contentId(1L)
                                .title("Test Content")
                                .status(ContentStatus.PUBLISHED)
                                .category(category)
                                .author(author)
                                .publishedAt(LocalDateTime.now())
                                .viewCount(0L)
                                .ratingCount(0L)
                                .build();

                PageRequest pageRequest = PageRequest.of(0, 10);

                // Match string matcher for status
                given(contentRepository.findByStatusAndDeletedAtIsNull(any(ContentStatus.class), any(Pageable.class)))
                                .willReturn(new PageImpl<>(List.of(content)));

                // when
                Page<ContentResponseDto> result = contentService.getContents(
                                new com.kz.magazine.dto.content.ContentFilterDto(),
                                pageRequest);

                // then
                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Content");
        }

        @Test
        @DisplayName("사보 상세 조회 성공")
        void getContent_Success() {
                // given
                Category category = Category.builder().categoryName("General").build();
                User author = User.builder().name("Author").build();

                Content content = Content.builder()
                                .contentId(1L)
                                .title("Detail")
                                .viewCount(0L)
                                .ratingCount(0L)
                                .status(ContentStatus.PUBLISHED)
                                .category(category)
                                .author(author)
                                .publishedAt(LocalDateTime.now())
                                .build();

                given(contentRepository.findById(1L)).willReturn(Optional.of(content));
                given(contentHashtagRepository.findHashtagNamesByContentId(1L)).willReturn(List.of());
                given(reactionRepository.findByContent_ContentId(1L)).willReturn(List.of());

                // when
                ContentDetailResponseDto result = contentService.getContent(1L, "user");

                // then
                assertThat(result.getTitle()).isEqualTo("Detail");
        }
}

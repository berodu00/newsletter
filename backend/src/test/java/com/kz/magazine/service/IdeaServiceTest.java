package com.kz.magazine.service;

import com.kz.magazine.dto.idea.IdeaRequestDto;
import com.kz.magazine.dto.idea.IdeaResponseDto;
import com.kz.magazine.entity.Idea;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.IdeaRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class IdeaServiceTest {

    @InjectMocks
    private IdeaService ideaService;

    @Mock
    private IdeaRepository ideaRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("아이디어 생성 성공")
    void createIdea_Success() {
        // given
        User user = User.builder().userId(1L).username("user").name("User Name").build();
        given(userRepository.findByUsername("user")).willReturn(Optional.of(user));

        Idea idea = Idea.builder()
                .ideaId(1L)
                .user(user)
                .title("New Idea")
                .description("Desc")
                .createdAt(java.time.LocalDateTime.now())
                .build();

        given(ideaRepository.save(any(Idea.class))).willReturn(idea);

        // when
        IdeaRequestDto dto = new IdeaRequestDto();
        dto.setTitle("New Idea");
        dto.setDescription("Desc");

        IdeaResponseDto result = ideaService.createIdea("user", dto);

        // then
        assertThat(result.getTitle()).isEqualTo("New Idea");
        assertThat(result.getAuthorName()).isEqualTo("User Name"); // Correct expectation
    }
}

package com.kz.magazine.controller;

import com.kz.magazine.dto.content.ContentFilterDto;
import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.service.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContentController.class)
@Import(com.kz.magazine.config.SecurityConfig.class)
public class SearchApiTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ContentService contentService;

        @MockBean
        private com.kz.magazine.service.ContentViewService contentViewService;

        @MockBean
        private com.kz.magazine.security.JwtTokenProvider jwtTokenProvider;

        @MockBean
        private com.kz.magazine.repository.UserRepository userRepository;

        @Test
        @DisplayName("Search Contents - Calls Service with Query")
        @WithMockUser
        void searchContents_Success() throws Exception {
                // Given
                ContentResponseDto dto = ContentResponseDto.builder()
                                .contentId(100L)
                                .title("Search Result Content")
                                .publishedAt(LocalDateTime.now())
                                .build();
                Page<ContentResponseDto> page = new PageImpl<>(java.util.Collections.singletonList(dto),
                                org.springframework.data.domain.PageRequest.of(0, 10), 1);

                given(contentService.getContents(
                                argThat(filter -> "testQuery".equals(filter.getQ())),
                                any()))
                                .willReturn(page);

                // When & Then
                mockMvc.perform(get("/api/contents")
                                .param("q", "testQuery"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].title").value("Search Result Content"));
        }
}

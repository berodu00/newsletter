package com.kz.magazine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.magazine.config.SecurityConfig;
import com.kz.magazine.dto.content.ContentCreateRequestDto;
import com.kz.magazine.dto.content.ContentDetailResponseDto;
import com.kz.magazine.dto.content.ContentFilterDto;
import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.dto.content.ContentUpdateRequestDto;
import com.kz.magazine.security.JwtAuthenticationFilter;
import com.kz.magazine.security.JwtTokenProvider;
import com.kz.magazine.service.ContentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ContentController.class, excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtAuthenticationFilter.class)
})
class ContentApiTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ContentService contentService;

        @MockBean
        private JwtTokenProvider jwtTokenProvider;

        @Test
        @DisplayName("Get Contents List - Success")
        @WithMockUser
        void getContents_Success() throws Exception {
                // Given
                ContentResponseDto dto = ContentResponseDto.builder()
                                .contentId(1L)
                                .title("Test Content")
                                .summary("Summary")
                                .categoryName("General")
                                .authorName("Admin")
                                .publishedAt(LocalDateTime.now())
                                .viewCount(10L)
                                .build();

                Page<ContentResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

                given(contentService.getContents(any(ContentFilterDto.class), any())).willReturn(page);

                // When & Then
                mockMvc.perform(get("/api/contents")
                                .param("page", "0")
                                .param("size", "10")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].title").value("Test Content"))
                                .andExpect(jsonPath("$.content[0].viewCount").value(10));
        }

        @Test
        @DisplayName("Get Content Detail - Success")
        @WithMockUser
        void getContent_Success() throws Exception {
                // Given
                ContentDetailResponseDto dto = ContentDetailResponseDto.builder()
                                .contentId(1L)
                                .title("Detail Title")
                                .bodyHtml("<p>Body</p>")
                                .categoryName("General")
                                .hashtags(List.of("Tag1", "Tag2"))
                                .build();

                given(contentService.getContent(1L)).willReturn(dto);

                // When & Then
                mockMvc.perform(get("/api/contents/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Detail Title"))
                                .andExpect(jsonPath("$.hashtags[0]").value("Tag1"));
        }

        @Test
        @DisplayName("Create Content - Success")
        @WithMockUser(roles = "ADMIN")
        void createContent_Success() throws Exception {
                // Given
                ContentCreateRequestDto request = new ContentCreateRequestDto();
                request.setTitle("New Content");
                request.setBodyHtml("<p>Body</p>");
                request.setCategoryName("General");
                request.setHashtags(List.of("NewTag"));

                ContentDetailResponseDto response = ContentDetailResponseDto.builder()
                                .contentId(2L)
                                .title("New Content")
                                .build();

                given(contentService.createContent(any(ContentCreateRequestDto.class), any())).willReturn(response);

                // When & Then
                ObjectMapper mapper = new ObjectMapper();
                mockMvc.perform(post("/api/contents")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("New Content"));
        }

        @Test
        @DisplayName("Update Content - Success")
        @WithMockUser(roles = "ADMIN")
        void updateContent_Success() throws Exception {
                // Given
                ContentUpdateRequestDto request = new ContentUpdateRequestDto();
                request.setTitle("Updated Title");

                ContentDetailResponseDto response = ContentDetailResponseDto.builder()
                                .contentId(1L)
                                .title("Updated Title")
                                .build();

                given(contentService.updateContent(any(Long.class), any(ContentUpdateRequestDto.class)))
                                .willReturn(response);

                // When & Then
                ObjectMapper mapper = new ObjectMapper();
                mockMvc.perform(put("/api/contents/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .contentType("application/json")
                                .content(mapper.writeValueAsString(request)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("Updated Title"));
        }

        @Test
        @DisplayName("Delete Content - Success")
        @WithMockUser(roles = "ADMIN")
        void deleteContent_Success() throws Exception {
                // When & Then
                mockMvc.perform(delete("/api/contents/1")
                                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                                .andExpect(status().isNoContent());
        }
}

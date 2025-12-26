package com.kz.magazine.controller;

import com.kz.magazine.dto.idea.IdeaRequestDto;
import com.kz.magazine.dto.idea.IdeaResponseDto;
import com.kz.magazine.dto.idea.IdeaReviewDto;
import com.kz.magazine.entity.IdeaStatus;
import com.kz.magazine.security.JwtTokenProvider;
import com.kz.magazine.service.IdeaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IdeaController.class)
@Import(com.kz.magazine.config.SecurityConfig.class)
@EnableMethodSecurity
class IdeaApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IdeaService ideaService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.kz.magazine.interceptor.VisitorInterceptor visitorInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "user")
    @DisplayName("Create Idea - Success")
    void createIdea_Success() throws Exception {
        IdeaRequestDto request = new IdeaRequestDto("Title", "Desc");
        IdeaResponseDto response = IdeaResponseDto.builder()
                .ideaId(1L)
                .title("Title")
                .description("Desc")
                .status(IdeaStatus.PENDING)
                .build();

        given(ideaService.createIdea(any(), any())).willReturn(response);

        mockMvc.perform(post("/api/ideas")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Review Idea - Admin Success")
    void reviewIdea_Success() throws Exception {
        IdeaReviewDto request = new IdeaReviewDto(IdeaStatus.ACCEPTED, "Good");
        IdeaResponseDto response = IdeaResponseDto.builder()
                .ideaId(1L)
                .status(IdeaStatus.ACCEPTED)
                .adminComment("Good")
                .build();

        given(ideaService.reviewIdea(eq(1L), any(), any())).willReturn(response);

        mockMvc.perform(put("/api/ideas/1/review")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));
    }
}

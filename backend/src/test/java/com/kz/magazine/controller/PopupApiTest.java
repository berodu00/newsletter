package com.kz.magazine.controller;

import com.kz.magazine.dto.popup.PopupRequestDto;
import com.kz.magazine.dto.popup.PopupResponseDto;
import com.kz.magazine.security.JwtTokenProvider;
import com.kz.magazine.service.PopupService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PopupController.class)
@Import(com.kz.magazine.config.SecurityConfig.class)
@EnableMethodSecurity
class PopupApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PopupService popupService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private com.kz.magazine.interceptor.VisitorInterceptor visitorInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    @DisplayName("Get Active Popups - Success")
    void getActivePopups_Success() throws Exception {
        PopupResponseDto dto = PopupResponseDto.builder()
                .popupId(1L)
                .title("Promo")
                .isActive(true)
                .build();

        given(popupService.getActivePopups()).willReturn(List.of(dto));

        mockMvc.perform(get("/api/popups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Promo"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    @DisplayName("Create Popup - Admin Success")
    void createPopup_Success() throws Exception {
        PopupRequestDto request = new PopupRequestDto();
        request.setTitle("New Popup");
        request.setStartAt(LocalDateTime.now());
        request.setEndAt(LocalDateTime.now().plusDays(1));

        PopupResponseDto response = PopupResponseDto.builder()
                .popupId(1L)
                .title("New Popup")
                .isActive(true)
                .build();

        given(popupService.createPopup(any(), any())).willReturn(response);

        mockMvc.perform(post("/api/popups")
                .with(csrf())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Popup"));
    }
}

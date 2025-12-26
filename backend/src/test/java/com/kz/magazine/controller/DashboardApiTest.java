package com.kz.magazine.controller;

import com.kz.magazine.dto.content.ContentResponseDto;
import com.kz.magazine.dto.dashboard.CategoryStatDto;
import com.kz.magazine.dto.dashboard.HashtagStatDto;
import com.kz.magazine.dto.dashboard.ReactionStatDto;
import com.kz.magazine.dto.dashboard.VisitorTrendDto;
import com.kz.magazine.service.DashboardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DashboardController.class)
@org.springframework.context.annotation.Import(com.kz.magazine.config.SecurityConfig.class)
public class DashboardApiTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private DashboardService dashboardService;

        @MockBean
        private com.kz.magazine.security.JwtTokenProvider jwtTokenProvider;

        @Test
        @DisplayName("Admin can access top views")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void getTopViewedContents_Admin_Success() throws Exception {
                given(dashboardService.getTopViewedContents(10))
                                .willReturn(Collections.emptyList());

                mockMvc.perform(get("/api/dashboard/top-views"))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("User cannot access top views")
        @WithMockUser(username = "user", roles = "USER")
        void getTopViewedContents_User_Forbidden() throws Exception {
                mockMvc.perform(get("/api/dashboard/top-views"))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Admin can access visitor trend")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void getVisitorTrend_Admin_Success() throws Exception {
                given(dashboardService.getVisitorTrend(7))
                                .willReturn(Collections.emptyList());

                mockMvc.perform(get("/api/dashboard/visitor-trend"))
                                .andExpect(status().isOk());
        }
}

package com.kz.magazine.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kz.magazine.entity.Event;
import com.kz.magazine.entity.EventParticipant;
import com.kz.magazine.entity.EventStatus;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.EventParticipantRepository;
import com.kz.magazine.repository.EventRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class EventApiTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private EventRepository eventRepository;

        @Autowired
        private EventParticipantRepository eventParticipantRepository;

        @Autowired
        private UserRepository userRepository;

        @BeforeEach
        void setUp() {
                eventParticipantRepository.deleteAll();
                eventRepository.deleteAll();
                // Do not delete users to avoid FK issues with other entities (AuditLog,
                // Content, etc.)
                // Instead, ensure they exist.

                // Admin User
                if (userRepository.findByUsername("admin").isEmpty()) {
                        userRepository.save(User.builder()
                                        .username("admin")
                                        .name("Admin User")
                                        .role("ADMIN")
                                        .isActive(true)
                                        .build());
                }

                // Normal User
                if (userRepository.findByUsername("user1").isEmpty()) {
                        userRepository.save(User.builder()
                                        .username("user1")
                                        .name("Normal User")
                                        .role("USER")
                                        .isActive(true)
                                        .build());
                }
        }

        @Test
        @DisplayName("Admin can create event")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void createEvent() throws Exception {
                Map<String, Object> request = Map.of(
                                "title", "New Event",
                                "description", "Description",
                                "startAt", LocalDateTime.now().plusDays(1).toString(),
                                "endAt", LocalDateTime.now().plusDays(5).toString());

                mockMvc.perform(post("/api/events")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.title").value("New Event"));
        }

        @Test
        @DisplayName("User can participate in event")
        @WithMockUser(username = "user1", roles = "USER")
        void participate() throws Exception {
                // given
                Event event = eventRepository.save(Event.builder()
                                .title("Active Event")
                                .startAt(LocalDateTime.now().minusDays(1))
                                .endAt(LocalDateTime.now().plusDays(1))
                                .status(EventStatus.ACTIVE)
                                .build());

                Map<String, String> request = Map.of("comment", "I want to verify!");

                // when & then
                mockMvc.perform(post("/api/events/" + event.getId() + "/participate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Admin can draw winners")
        @WithMockUser(username = "admin", roles = "ADMIN")
        void drawWinners() throws Exception {
                // given
                User user1 = userRepository.findByUsername("user1").get();
                // create more users for draw
                User user2 = userRepository.findByUsername("user2")
                                .orElseGet(() -> userRepository.save(User.builder().username("user2").name("User 2")
                                                .role("USER").isActive(true).build()));

                Event event = eventRepository.save(Event.builder()
                                .title("Draw Event")
                                .startAt(LocalDateTime.now().minusDays(1))
                                .endAt(LocalDateTime.now().plusDays(1))
                                .status(EventStatus.ACTIVE)
                                .build());

                eventParticipantRepository
                                .save(EventParticipant.builder().event(event).user(user1).isWinner(false).build());
                eventParticipantRepository
                                .save(EventParticipant.builder().event(event).user(user2).isWinner(false).build());

                Map<String, Integer> request = Map.of("count", 1);

                // when & then
                mockMvc.perform(post("/api/events/" + event.getId() + "/draw")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andDo(print())
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].winner").value(true));
        }
}

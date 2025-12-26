package com.kz.magazine.service;

import com.kz.magazine.entity.Event;
import com.kz.magazine.entity.EventParticipant;
import com.kz.magazine.entity.EventStatus;
import com.kz.magazine.entity.User;
import com.kz.magazine.repository.EventParticipantRepository;
import com.kz.magazine.repository.EventRepository;
import com.kz.magazine.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventParticipantRepository eventParticipantRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuditLogService auditLogService;

    @Test
    @DisplayName("Duplicate participation should fail")
    void participate_Duplicate() {
        // given
        Long eventId = 1L;
        String username = "user1";
        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(User.builder().username(username).build()));
        given(eventRepository.findByIdAndDeletedAtIsNull(eventId)).willReturn(Optional.of(Event.builder()
                .startAt(LocalDateTime.now().minusDays(1))
                .endAt(LocalDateTime.now().plusDays(1))
                .status(EventStatus.ACTIVE)
                .build()));
        given(eventParticipantRepository.existsByEvent_IdAndUser_Username(eventId, username)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> eventService.participate(eventId, username, "comment"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Already participated");
    }

    @Test
    @DisplayName("Draw winners should select random participants")
    void drawWinners_Success() {
        // given
        Long eventId = 1L;
        String username = "admin";
        int count = 2;

        given(userRepository.findByUsername(username))
                .willReturn(Optional.of(User.builder().username(username).build()));
        given(eventRepository.findByIdAndDeletedAtIsNull(eventId)).willReturn(Optional.of(Event.builder().build()));

        List<EventParticipant> mocks = List.of(
                EventParticipant.builder().isWinner(false).build(),
                EventParticipant.builder().isWinner(false).build());
        given(eventParticipantRepository.findRandomParticipants(eventId, count)).willReturn(mocks);

        // when
        List<EventParticipant> winners = eventService.drawWinners(eventId, count, username);

        // then
        assertThat(winners).hasSize(2);
        assertThat(winners.get(0).isWinner()).isTrue();
        assertThat(winners.get(1).isWinner()).isTrue();
        verify(auditLogService).logAction(any(), any(), any(), any(), any(), any());
    }
}

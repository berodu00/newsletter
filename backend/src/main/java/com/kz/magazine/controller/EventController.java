package com.kz.magazine.controller;

import com.kz.magazine.entity.Event;
import com.kz.magazine.entity.EventParticipant;
import com.kz.magazine.entity.EventStatus;
import com.kz.magazine.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<Page<Event>> getEvents(
            @RequestParam(required = false) String status,
            @PageableDefault(sort = "startAt") Pageable pageable) {
        return ResponseEntity.ok(eventService.getEvents(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEvent(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> createEvent(@RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        Long thumbnailFileId = request.get("thumbnailFileId") != null
                ? ((Number) request.get("thumbnailFileId")).longValue()
                : null;
        LocalDateTime startAt = LocalDateTime.parse((String) request.get("startAt"));
        LocalDateTime endAt = LocalDateTime.parse((String) request.get("endAt"));

        Event event = eventService.createEvent(title, description, thumbnailFileId, startAt, endAt,
                userDetails.getUsername());
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Event> updateEvent(@PathVariable Long id, @RequestBody Map<String, Object> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String title = (String) request.get("title");
        String description = (String) request.get("description");
        Long thumbnailFileId = request.get("thumbnailFileId") != null
                ? ((Number) request.get("thumbnailFileId")).longValue()
                : null;
        LocalDateTime startAt = LocalDateTime.parse((String) request.get("startAt"));
        LocalDateTime endAt = LocalDateTime.parse((String) request.get("endAt"));
        EventStatus status = EventStatus.valueOf((String) request.get("status"));

        Event event = eventService.updateEvent(id, title, description, thumbnailFileId, startAt, endAt, status,
                userDetails.getUsername());
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        eventService.deleteEvent(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/participate")
    public ResponseEntity<Void> participate(@PathVariable Long id, @RequestBody Map<String, String> request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String comment = request.get("comment");
        eventService.participate(id, userDetails.getUsername(), comment);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/draw")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventParticipant>> drawWinners(@PathVariable Long id,
            @RequestBody Map<String, Integer> request, @AuthenticationPrincipal UserDetails userDetails) {
        int count = request.get("count");
        List<EventParticipant> winners = eventService.drawWinners(id, count, userDetails.getUsername());
        return ResponseEntity.ok(winners);
    }

    @GetMapping("/{id}/winners")
    public ResponseEntity<List<EventParticipant>> getWinners(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getWinners(id));
    }
}
// Touched for reload

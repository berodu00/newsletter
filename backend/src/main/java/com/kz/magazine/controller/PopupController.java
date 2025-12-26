package com.kz.magazine.controller;

import com.kz.magazine.dto.popup.PopupRequestDto;
import com.kz.magazine.dto.popup.PopupResponseDto;
import com.kz.magazine.service.PopupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/popups")
@RequiredArgsConstructor
public class PopupController {

    private final PopupService popupService;

    // Public or User? (Usually popups are for main page, so maybe PermitAll?)
    // But V1 says /api/popups (active)
    // Let's assume permitAll in SecurityConfig or Authenticated.
    // Dashboard usage implies Admin.
    // Frontend task 4.12 says "Main Page Popup Modal". So it should be public or
    // user accessible.

    @GetMapping
    public List<PopupResponseDto> getActivePopups() {
        return popupService.getActivePopups();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PopupResponseDto> getAllPopups() {
        return popupService.getAllPopups();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PopupResponseDto createPopup(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PopupRequestDto dto) {
        return popupService.createPopup(userDetails.getUsername(), dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PopupResponseDto updatePopup(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody PopupRequestDto dto) {
        return popupService.updatePopup(id, userDetails.getUsername(), dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deletePopup(@PathVariable Long id) {
        popupService.deletePopup(id);
    }
}

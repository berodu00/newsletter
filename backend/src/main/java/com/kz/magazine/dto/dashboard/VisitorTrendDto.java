package com.kz.magazine.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitorTrendDto {
    private LocalDate date;
    private Long pageViews;
    private Long visitorCount; // Optional if we track unique users per day
}

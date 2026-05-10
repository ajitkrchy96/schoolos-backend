package com.school.dto.dashboard;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardResponseDTO {

    private long totalStudents;

    private long presentToday;
    private long absentToday;

    private long pendingFeesCount;
    private double totalPendingAmount;

    private double totalCollectedAmount;
}
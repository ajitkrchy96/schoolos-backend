package com.school.dto.attendance;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttendanceSummaryDTO {

    private long presentCount;
    private long absentCount;
}
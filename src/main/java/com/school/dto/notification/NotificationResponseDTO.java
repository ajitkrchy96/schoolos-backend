package com.school.dto.notification;


import com.school.utilenum.NotificationStatus;
import com.school.utilenum.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {

    private Long id;

    private Long studentId;

    private Long parentId;

    private NotificationType type;

    private String recipientPhone;

    private String message;

    private NotificationStatus status;

    private String providerResponse;

    private LocalDateTime sentAt;
}
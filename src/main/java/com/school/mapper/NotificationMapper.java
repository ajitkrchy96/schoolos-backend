package com.school.mapper;

import com.school.dto.notification.NotificationResponseDTO;
import com.school.model.NotificationLog;

import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponseDTO toDTO(NotificationLog log) {

        return NotificationResponseDTO.builder()
                .id(log.getId())
                .studentId(
                        log.getStudent() != null
                                ? log.getStudent().getId()
                                : null
                )
                .parentId(
                        log.getParent() != null
                                ? log.getParent().getId()
                                : null
                )
                .type(log.getType())
                .recipientPhone(log.getRecipientPhone())
                .message(log.getMessage())
                .status(log.getStatus())
                .providerResponse(log.getProviderResponse())
                .sentAt(log.getSentAt())
                .build();
    }
}
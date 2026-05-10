package com.school.dto.notification;

import com.school.utilenum.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequestDTO {

    private Long studentId;

    private Long parentId;

    @NotBlank
    @Size(max = 15)
    private String phone;

    @NotBlank
    @Size(max = 1000)
    private String message;

    @NotNull
    private NotificationType type;
}
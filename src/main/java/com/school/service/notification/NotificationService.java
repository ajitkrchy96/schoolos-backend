package com.school.service.notification;

import com.school.dto.notification.NotificationRequestDTO;
import com.school.dto.notification.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {

    void sendFeeReminder(Long schoolId, Long studentId);

    void sendAttendanceAlert(Long schoolId, Long studentId);

    void sendAnnouncement(Long schoolId, NotificationRequestDTO dto);

    List<NotificationResponseDTO> getNotificationHistory(Long schoolId);
}
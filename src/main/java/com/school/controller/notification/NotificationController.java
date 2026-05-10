package com.school.controller.notification;

import com.school.dto.notification.NotificationRequestDTO;
import com.school.dto.notification.NotificationResponseDTO;
import com.school.service.notification.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools/{schoolId}/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/fee-reminder/{studentId}")
    public ResponseEntity<Void> sendFeeReminder(@PathVariable Long schoolId, @PathVariable Long studentId) {
        notificationService.sendFeeReminder(schoolId, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/attendance-alert/{studentId}")
    public ResponseEntity<Void> sendAttendanceAlert(@PathVariable Long schoolId, @PathVariable Long studentId) {
        notificationService.sendAttendanceAlert(schoolId, studentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/announcement")
    public ResponseEntity<Void> sendAnnouncement(@PathVariable Long schoolId, @Valid @RequestBody NotificationRequestDTO dto) {
        notificationService.sendAnnouncement(schoolId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<NotificationResponseDTO>> getHistory(@PathVariable Long schoolId) {
        List<NotificationResponseDTO> history = notificationService.getNotificationHistory(schoolId);
        return ResponseEntity.ok(history);
    }
}
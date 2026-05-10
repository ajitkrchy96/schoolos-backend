package com.school.service.notification.impl;

import com.school.dto.notification.NotificationRequestDTO;
import com.school.dto.notification.NotificationResponseDTO;
import com.school.exception.ResourceNotFoundException;
import com.school.exception.ValidationException;
import com.school.mapper.NotificationMapper;
import com.school.model.NotificationLog;
import com.school.model.Parent;
import com.school.model.School;
import com.school.model.Student;
import com.school.repository.*;
import com.school.service.notification.NotificationService;
import com.school.util.WhatsAppClient;
import com.school.utilenum.NotificationStatus;
import com.school.utilenum.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final StudentRepository studentRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final ParentRepository parentRepository;
    private final SchoolRepository schoolRepository;
    //private final FeeRepository feeRepository;
    private final WhatsAppClient whatsAppClient;
    private final NotificationMapper mapper;

    @Override
    @Transactional
    public void sendFeeReminder(Long schoolId, Long studentId) {
        validateSchool(schoolId);

        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Parent parent = student.getParent();
        if (parent == null || parent.getPhone() == null) {
            throw new ValidationException("Parent phone not available");
        }

        Double dueAmount = studentFeeRepository.getTotalDueAmount(studentId, schoolId);
        if (dueAmount == null || dueAmount <= 0) {
            throw new ValidationException("No due fees for the student");
        }

        String message = "Dear Parent, fee of Rs. " + dueAmount + " is pending for student " + student.getFirstName() + " " + student.getLastName() + ".";

        sendNotificationToParent(student, parent, message, NotificationType.FEE_REMINDER);
    }

    @Override
    @Transactional
    public void sendAttendanceAlert(Long schoolId, Long studentId) {
        validateSchool(schoolId);
        Student student = studentRepository.findByIdAndSchoolId(studentId, schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Parent parent = student.getParent();
        if (parent == null || parent.getPhone() == null) {
            throw new ValidationException("Parent phone not available");
        }

        String message = "Student " + student.getFirstName() + " " + student.getLastName() + " is absent today.";

        sendNotificationToParent(student, parent, message, NotificationType.ATTENDANCE_ALERT);
    }

    @Override
    @Transactional
    public void sendAnnouncement(Long schoolId, NotificationRequestDTO dto) {
        School school = schoolRepository.findById(schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));
        if (dto.getParentId() == null) {
            throw new ValidationException("Parent ID is required for announcement");
        }
        Parent parent = parentRepository.findByPhoneAndSchoolId(dto.getPhone(), schoolId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent with phone not found"));

        NotificationStatus status = whatsAppClient.sendMessage(parent.getPhone(), dto.getMessage());

        NotificationLog log = new NotificationLog();
        log.setSchool(school);
        if (dto.getStudentId() != null) {
            Student student = studentRepository.findByIdAndSchoolId(dto.getStudentId(), schoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
            log.setStudent(student);
        }

        log.setParent(parent);
        log.setType(dto.getType());
        log.setRecipientPhone(dto.getPhone());
        log.setMessage(dto.getMessage());
        log.setProviderResponse("Mock WhatsApp API Success");//TODO
        log.setStatus(status);
        log.setSentAt(LocalDateTime.now());

        notificationLogRepository.save(log);
    }

    @Override
    public List<NotificationResponseDTO> getNotificationHistory(Long schoolId) {
        List<NotificationLog> logs = notificationLogRepository.findBySchoolIdOrderBySentAtDesc(schoolId);
        return logs.stream().map(mapper::toDTO).toList();
    }

    private void validateSchool(Long schoolId) {
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
    }

    private void sendNotificationToParent(Student student, Parent parent, String message, NotificationType type) {
        NotificationStatus status = whatsAppClient.sendMessage(parent.getPhone(), message);

        NotificationLog log = new NotificationLog();
        log.setSchool(student.getSchool());
        log.setStudent(student);
        log.setParent(parent);
        log.setType(type);
        log.setRecipientPhone(parent.getPhone());
        log.setMessage(message);
        log.setProviderResponse("Mock WhatsApp API Success");//TODO need to check
        log.setStatus(status);
        log.setSentAt(LocalDateTime.now());

        notificationLogRepository.save(log);
    }
}
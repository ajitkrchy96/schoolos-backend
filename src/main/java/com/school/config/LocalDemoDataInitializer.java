package com.school.config;

import com.school.model.AppUser;
import com.school.model.ClassEntity;
import com.school.model.FeeStructure;
import com.school.model.Parent;
import com.school.model.School;
import com.school.model.Section;
import com.school.model.Student;
import com.school.model.StudentAttendance;
import com.school.model.StudentFee;
import com.school.model.FeePayment;
import com.school.model.NotificationLog;
import com.school.repository.AppUserRepository;
import com.school.repository.ClassEntityRepository;
import com.school.repository.FeePaymentRepository;
import com.school.repository.NotificationLogRepository;
import com.school.repository.FeeStructureRepository;
import com.school.repository.ParentRepository;
import com.school.repository.SchoolRepository;
import com.school.repository.SectionRepository;
import com.school.repository.StudentAttendanceRepository;
import com.school.repository.StudentFeeRepository;
import com.school.repository.StudentRepository;
import com.school.utilenum.AttendanceStatus;
import com.school.utilenum.FeeFrequency;
import com.school.utilenum.FeeStatus;
import com.school.utilenum.PaymentMode;
import com.school.utilenum.NotificationStatus;
import com.school.utilenum.NotificationType;
import com.school.utilenum.StudentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** Seeds predictable, non-destructive sample data only when the local-demo profile is active. */
@Component
@Profile("local-demo")
public class LocalDemoDataInitializer implements CommandLineRunner {

    private final SchoolRepository schoolRepository;
    private final ClassEntityRepository classRepository;
    private final SectionRepository sectionRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final StudentAttendanceRepository attendanceRepository;
    private final FeeStructureRepository feeStructureRepository;
    private final StudentFeeRepository studentFeeRepository;
    private final FeePaymentRepository feePaymentRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;

    public LocalDemoDataInitializer(
            SchoolRepository schoolRepository,
            ClassEntityRepository classRepository,
            SectionRepository sectionRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            StudentAttendanceRepository attendanceRepository,
            FeeStructureRepository feeStructureRepository,
            StudentFeeRepository studentFeeRepository,
            FeePaymentRepository feePaymentRepository,
            AppUserRepository appUserRepository,
            NotificationLogRepository notificationLogRepository,
            PasswordEncoder passwordEncoder,
            @Value("${schoolos.demo.admin.username:}") String adminUsername,
            @Value("${schoolos.demo.admin.password:}") String adminPassword) {
        this.schoolRepository = schoolRepository;
        this.classRepository = classRepository;
        this.sectionRepository = sectionRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.feeStructureRepository = feeStructureRepository;
        this.studentFeeRepository = studentFeeRepository;
        this.feePaymentRepository = feePaymentRepository;
        this.appUserRepository = appUserRepository;
        this.notificationLogRepository = notificationLogRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!StringUtils.hasText(adminUsername) || !StringUtils.hasText(adminPassword)) {
            throw new IllegalStateException(
                    "Set SCHOOLOS_DEMO_ADMIN_USERNAME and SCHOOLOS_DEMO_ADMIN_PASSWORD " +
                            "before enabling the local-demo profile.");
        }

        School school = getOrCreateSchool();
        ClassEntity gradeNine = getOrCreateClass(school, "Grade 9");
        ClassEntity gradeTen = getOrCreateClass(school, "Grade 10");
        Section sectionA = getOrCreateSection(school, gradeTen, "A");
        Section sectionB = getOrCreateSection(school, gradeTen, "B");
        Section sectionNineA = getOrCreateSection(school, gradeNine, "A");

        Parent parentOne = getOrCreateParent(school, "9000000001", "Alex Morgan", "Taylor Morgan");
        Parent parentTwo = getOrCreateParent(school, "9000000002", "Jordan Lee", "Casey Lee");
        Parent parentThree = getOrCreateParent(school, "9000000003", "Riley Patel", "Sam Patel");

        Student studentOne = getOrCreateStudent(school, parentOne, gradeTen, sectionA,
                "DEMO-001", "Avery", "Morgan", "Female", LocalDate.of(2010, 5, 10), "9000000101");
        Student studentTwo = getOrCreateStudent(school, parentTwo, gradeTen, sectionB,
                "DEMO-002", "Cameron", "Lee", "Male", LocalDate.of(2010, 8, 14), "9000000102");
        Student studentThree = getOrCreateStudent(school, parentThree, gradeNine, sectionNineA,
                "DEMO-003", "Quinn", "Patel", "Other", LocalDate.of(2011, 2, 22), "9000000103");

        ensureAttendance(school, studentOne, LocalDate.now(), AttendanceStatus.PRESENT);
        ensureAttendance(school, studentTwo, LocalDate.now(), AttendanceStatus.ABSENT);
        ensureAttendance(school, studentThree, LocalDate.now(), AttendanceStatus.PRESENT);
        ensureAttendance(school, studentOne, LocalDate.now().minusDays(1), AttendanceStatus.PRESENT);
        ensureAttendance(school, studentTwo, LocalDate.now().minusDays(1), AttendanceStatus.PRESENT);
        ensureAttendance(school, studentThree, LocalDate.now().minusDays(1), AttendanceStatus.ABSENT);

        FeeStructure gradeTenFees = getOrCreateFeeStructure(school, gradeTen, "Demo tuition", new BigDecimal("3000.00"));
        FeeStructure gradeNineFees = getOrCreateFeeStructure(school, gradeNine, "Demo tuition", new BigDecimal("2500.00"));
        StudentFee feeOne = getOrCreateStudentFee(school, studentOne, gradeTenFees, new BigDecimal("3000.00"), new BigDecimal("3000.00"), FeeStatus.PAID);
        getOrCreateStudentFee(school, studentTwo, gradeTenFees, new BigDecimal("3000.00"), new BigDecimal("0.00"), FeeStatus.PENDING);
        StudentFee feeThree = getOrCreateStudentFee(school, studentThree, gradeNineFees, new BigDecimal("2500.00"), new BigDecimal("1000.00"), FeeStatus.PARTIAL);
        ensurePayment(school, feeOne, new BigDecimal("3000.00"), "DEMO-RCPT-001");
        ensurePayment(school, feeThree, new BigDecimal("1000.00"), "DEMO-RCPT-003");

        ensureNotification(school, studentTwo, "Local demo attendance alert for DEMO-002", NotificationType.ATTENDANCE_ALERT);
        ensureNotification(school, studentThree, "Local demo fee reminder for DEMO-003", NotificationType.FEE_REMINDER);

        ensureAdminUser(school);
    }

    private School getOrCreateSchool() {
        return schoolRepository.findById(1L).orElseGet(() -> {
            School school = new School();
            school.setName("SchoolOS Local Demo");
            school.setAddress("Local development sample data");
            school.setPhone("9000000000");
            school.setEmail("demo@schoolos.local");
            school.setSubscriptionPlan("LOCAL_DEMO");
            return schoolRepository.save(school);
        });
    }

    private ClassEntity getOrCreateClass(School school, String name) {
        return classRepository.findBySchoolIdAndName(school.getId(), name).orElseGet(() -> {
            ClassEntity entity = new ClassEntity();
            entity.setSchool(school);
            entity.setName(name);
            return classRepository.save(entity);
        });
    }

    private Section getOrCreateSection(School school, ClassEntity classEntity, String name) {
        return sectionRepository.findBySchoolIdAndClassEntityIdAndName(school.getId(), classEntity.getId(), name)
                .orElseGet(() -> {
                    Section section = new Section();
                    section.setSchool(school);
                    section.setClassEntity(classEntity);
                    section.setName(name);
                    return sectionRepository.save(section);
                });
    }

    private Parent getOrCreateParent(School school, String phone, String fatherName, String motherName) {
        return parentRepository.findByPhoneAndSchoolId(phone, school.getId()).orElseGet(() -> {
            Parent parent = new Parent();
            parent.setSchool(school);
            parent.setPhone(phone);
            parent.setFatherName(fatherName);
            parent.setMotherName(motherName);
            parent.setEmail("parent" + phone.substring(phone.length() - 1) + "@schoolos.local");
            parent.setAddress("Local demo address");
            return parentRepository.save(parent);
        });
    }

    private Student getOrCreateStudent(School school, Parent parent, ClassEntity classEntity, Section section,
                                       String admissionNo, String firstName, String lastName, String gender,
                                       LocalDate dob, String phone) {
        return studentRepository.findByAdmissionNoAndSchoolId(admissionNo, school.getId()).orElseGet(() -> {
            Student student = new Student();
            student.setSchool(school);
            student.setParent(parent);
            student.setClassEntity(classEntity);
            student.setSection(section);
            student.setAdmissionNo(admissionNo);
            student.setFirstName(firstName);
            student.setLastName(lastName);
            student.setGender(gender);
            student.setDob(dob);
            student.setPhone(phone);
            student.setStatus(StudentStatus.ACTIVE);
            return studentRepository.save(student);
        });
    }

    private void ensureAttendance(School school, Student student, LocalDate date, AttendanceStatus status) {
        if (attendanceRepository.findByStudentIdAndDateAndSchoolId(student.getId(), date, school.getId()).isEmpty()) {
            StudentAttendance attendance = new StudentAttendance();
            attendance.setSchool(school);
            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setStatus(status);
            attendanceRepository.save(attendance);
        }
    }

    private FeeStructure getOrCreateFeeStructure(School school, ClassEntity classEntity, String name, BigDecimal amount) {
        return feeStructureRepository.findBySchoolIdAndClassEntityIdAndName(school.getId(), classEntity.getId(), name)
                .orElseGet(() -> {
                    FeeStructure structure = new FeeStructure();
                    structure.setSchool(school);
                    structure.setClassEntity(classEntity);
                    structure.setName(name);
                    structure.setAmount(amount);
                    structure.setFrequency(FeeFrequency.MONTHLY);
                    structure.setDueDay(10);
                    structure.setFinePerDay(BigDecimal.ZERO);
                    return feeStructureRepository.save(structure);
                });
    }

    private StudentFee getOrCreateStudentFee(School school, Student student, FeeStructure structure,
                                             BigDecimal total, BigDecimal paid, FeeStatus status) {
        return studentFeeRepository.findByStudentIdAndSchoolId(student.getId(), school.getId()).orElseGet(() -> {
            StudentFee fee = new StudentFee();
            fee.setSchool(school);
            fee.setStudent(student);
            fee.setFeeStructure(structure);
            fee.setTotalAmount(total);
            fee.setPaidAmount(paid);
            fee.setDueAmount(total.subtract(paid));
            fee.setStatus(status);
            fee.setDueDate(LocalDate.now().plusDays(10));
            return studentFeeRepository.save(fee);
        });
    }

    private void ensurePayment(School school, StudentFee studentFee, BigDecimal amount, String receiptNumber) {
        List<FeePayment> payments = feePaymentRepository.findByStudentFeeIdAndSchoolIdOrderByPaymentDateDesc(
                studentFee.getId(), school.getId());
        if (payments.stream().noneMatch(payment -> receiptNumber.equals(payment.getReceiptNumber()))) {
            FeePayment payment = new FeePayment();
            payment.setSchool(school);
            payment.setStudentFee(studentFee);
            payment.setAmount(amount);
            payment.setPaymentDate(LocalDateTime.now());
            payment.setPaymentMode(PaymentMode.CASH);
            payment.setTransactionId(receiptNumber);
            payment.setReceiptNumber(receiptNumber);
            payment.setRemarks("Local demo seed payment");
            feePaymentRepository.save(payment);
        }
    }

    private void ensureAdminUser(School school) {
        if (appUserRepository.findByUsername(adminUsername).isEmpty()) {
            AppUser user = new AppUser();
            user.setSchool(school);
            user.setUsername(adminUsername);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRole("ADMIN");
            appUserRepository.save(user);
        }
    }

    private void ensureNotification(School school, Student student, String message, NotificationType type) {
        if (notificationLogRepository.findBySchoolIdAndMessage(school.getId(), message).isEmpty()) {
            NotificationLog notification = new NotificationLog();
            notification.setSchool(school);
            notification.setStudent(student);
            notification.setParent(student.getParent());
            notification.setType(type);
            notification.setRecipientPhone(student.getParent().getPhone());
            notification.setMessage(message);
            notification.setStatus(NotificationStatus.SUCCESS);
            notification.setProviderResponse("Local demo record; no external message was sent");
            notification.setSentAt(LocalDateTime.now());
            notificationLogRepository.save(notification);
        }
    }
}
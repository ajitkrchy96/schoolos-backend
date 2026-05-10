-- ===============================
-- SCHOOL
-- ===============================
INSERT INTO schoolos.school (id, name, address, phone, email, subscription_plan)
VALUES (1, 'Test School', 'Hyderabad', '9999999999', '[test@mail.com](mailto:test@mail.com)', 'BASIC');

-- ===============================
-- CLASS
-- ===============================
INSERT INTO schoolos.class (id, school_id, name)
VALUES (1, 1, '10th');

-- ===============================
-- SECTION
-- ===============================
INSERT INTO schoolos.section (id, school_id, class_id, name)
VALUES (1, 1, 1, 'A');

-- ===============================
-- PARENT
-- ===============================
INSERT INTO schoolos.parent (id, school_id, father_name, mother_name, phone)
VALUES (1, 1, 'Ramesh Kumar', 'Sita Devi', '9876543210');

-- ===============================
-- STUDENT
-- ===============================
INSERT INTO schoolos.student (
id, school_id, parent_id, class_id, section_id,
first_name, last_name, gender, dob, admission_no, phone, status
)
VALUES (
1, 1, 1, 1, 1,
'Ajit', 'Kumar', 'Male', '2005-05-10', 'A101', '9876543210', 'ACTIVE'
);

-- ===============================
-- FEE STRUCTURE
-- ===============================
INSERT INTO schoolos.fee_structure (
id, school_id, class_id, name, amount, frequency, due_day, fine_per_day
)
VALUES (
1, 1, 1, 'Tuition Fee', 2000, 'MONTHLY', 10, 10
);

INSERT INTO schoolos.student_attendance (
    id, school_id, student_id, date, status
)
VALUES
    (1, 1, 1, CURRENT_DATE, 'PRESENT');

INSERT INTO schoolos.app_user (id, school_id, username, password, role)
VALUES (1, 1, 'admin', '$2a$10$7EqJtq98hPqEX7fNZaFWoOHi6R2uR0zW0h1s7lG8e0t9kK7w3ZJ6K', 'ADMIN');

-- ===============================
-- NOTIFICATION LOG
-- ===============================
INSERT INTO schoolos.notification_log (
    id,
    school_id,
    student_id,
    parent_id,
    type,
    recipient_phone,
    message,
    status,
    provider_response,
    sent_at
)
VALUES (
           1,
           1,
           1,
           1,
           'FEE_REMINDER',
           '9876543210',
           'Test fee reminder',
           'SUCCESS',
           'Mock WhatsApp API Success',
           NOW()
       );

UPDATE schoolos.fee_payment
SET receipt_number = 'RCPT-1001'
WHERE id = 1;

UPDATE schoolos.fee_payment
SET receipt_number = 'RCPT-1001'
WHERE id = 1;


INSERT INTO schoolos.fee_payment (
    id,
    school_id,
    student_fee_id,
    amount,
    payment_date,
    payment_mode,
    transaction_id,
    remarks,
    receipt_number,
    created_at,
    updated_at
)
VALUES (
           1,
           1,
           1,
           1000.00,
           NOW(),
           'CASH',
           'TXN-10001',
           'First installment payment',
           'RCPT-1001',
           NOW(),
           NOW()
       );

-- ===============================
-- DROP ALL TABLES (SAFE RESET)
-- ===============================
DROP TABLE IF EXISTS schoolos.fee_payment CASCADE;
DROP TABLE IF EXISTS schoolos.student_fee CASCADE;
DROP TABLE IF EXISTS schoolos.fee_structure CASCADE;
DROP TABLE IF EXISTS schoolos.student CASCADE;
DROP TABLE IF EXISTS schoolos.parent CASCADE;
DROP TABLE IF EXISTS schoolos.section CASCADE;
DROP TABLE IF EXISTS schoolos.class CASCADE;
DROP TABLE IF EXISTS schoolos.teacher CASCADE;
DROP TABLE IF EXISTS schoolos.school CASCADE;
DROP TABLE IF EXISTS schoolos.student_attendance CASCADE;
DROP TABLE IF EXISTS schoolos.app_user CASCADE;
DROP TABLE IF EXISTS schoolos.notification_log CASCADE;

-- ===============================
-- SCHOOL
-- ===============================
CREATE TABLE schoolos.school (
id BIGSERIAL PRIMARY KEY,
name VARCHAR(255),
address TEXT,
phone VARCHAR(20),
email VARCHAR(255),
subscription_plan VARCHAR(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- CLASS
-- ===============================
CREATE TABLE schoolos.class (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
name VARCHAR(50),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- SECTION
-- ===============================
CREATE TABLE schoolos.section (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
class_id BIGINT,
name VARCHAR(10),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- PARENT
-- ===============================
CREATE TABLE schoolos.parent (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
father_name VARCHAR(255),
mother_name VARCHAR(255),
phone VARCHAR(20),
email VARCHAR(255),
address TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- STUDENT
-- ===============================
CREATE TABLE schoolos.student (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
parent_id BIGINT,
class_id BIGINT,
section_id BIGINT,
first_name VARCHAR(100),
last_name VARCHAR(100),
gender VARCHAR(10),
dob DATE,
admission_no VARCHAR(50),
phone VARCHAR(20),
status VARCHAR(20),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- FEE STRUCTURE
-- ===============================
CREATE TABLE schoolos.fee_structure (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
class_id BIGINT,
name VARCHAR(100),
amount NUMERIC(10,2),
frequency VARCHAR(20),
due_day INT,
fine_per_day NUMERIC(10,2),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- STUDENT FEE
-- ===============================
CREATE TABLE schoolos.student_fee (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
student_id BIGINT,
fee_structure_id BIGINT,
total_amount NUMERIC(10,2),
paid_amount NUMERIC(10,2),
due_amount NUMERIC(10,2),
status VARCHAR(20),
due_date DATE,
last_payment_date TIMESTAMP,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ===============================
-- FEE PAYMENT
-- ===============================
CREATE TABLE schoolos.fee_payment (
id BIGSERIAL PRIMARY KEY,
school_id BIGINT,
student_fee_id BIGINT,
amount NUMERIC(10,2),
payment_date TIMESTAMP,
payment_mode VARCHAR(20),
transaction_id VARCHAR(255),
remarks TEXT,
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
-- ===============================
-- STUDENT ATTENDANCE
-- ===============================
CREATE TABLE schoolos.student_attendance (
                                             id BIGSERIAL PRIMARY KEY,
                                             school_id BIGINT NOT NULL,
                                             student_id BIGINT NOT NULL,
                                             date DATE NOT NULL,
                                             status VARCHAR(10), -- PRESENT / ABSENT

                                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                             UNIQUE (student_id, date)

);

-- ===============================
-- USER TABLE
-- ===============================
CREATE TABLE schoolos.app_user (
                                   id BIGSERIAL PRIMARY KEY,
                                   school_id BIGINT,
                                   username VARCHAR(100) UNIQUE,
                                   password VARCHAR(255),
                                   role VARCHAR(20), -- ADMIN / TEACHER

                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP

);

-- ===============================
-- NOTIFICATION LOG
-- ===============================
CREATE TABLE schoolos.notification_log (
    id BIGSERIAL PRIMARY KEY,
    school_id BIGINT NOT NULL,
    student_id BIGINT,
    parent_id BIGINT,
    type VARCHAR(20) NOT NULL,
    recipient_phone VARCHAR(15) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(10) NOT NULL,
    provider_response TEXT,
    sent_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE schoolos.fee_payment
    ADD COLUMN receipt_number VARCHAR(100);

ALTER TABLE schoolos.app_user
    ADD COLUMN reference_id VARCHAR(255);

ALTER TABLE schoolos.student_attendance
    ADD CONSTRAINT uk_student_attendance_date
        UNIQUE(student_id, date);

ALTER TABLE schoolos.student
ALTER COLUMN admission_no TYPE VARCHAR(50)
;

ALTER TABLE schoolos.student
ALTER COLUMN phone TYPE VARCHAR(50)
;

-- Removed forced convert_from/bytea handling: ensure columns are VARCHAR in DB


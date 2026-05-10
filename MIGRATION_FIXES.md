# Database Column Naming Fixes - SchoolOS Backend

## Problem Fixed
**Error**: `org.postgresql.util.PSQLException: ERROR: column s1_0.class_entity_id does not exist`

The issue was that Hibernate JPA was using incorrect column names for relationships because `@JoinColumn` annotations were missing or incomplete on many entity models.

## Root Cause
When `@JoinColumn` annotation is omitted from a `@ManyToOne` relationship, Hibernate defaults to naming the foreign key column as `fieldName_id`. This caused:
- Field named `classEntity` → Column name `class_entity_id` ❌ (database has `class_id`)
- Missing explicit `@JoinColumn` specifications across multiple entities

## Solution Applied
Added explicit `@JoinColumn` annotations with correct column names and `FetchType.LAZY` to all entity models.

## Entities Fixed

### Fixed Entities (13 total):

1. **Student.java**
   - school: `@JoinColumn(name = "school_id")`
   - parent: `@JoinColumn(name = "parent_id")`
   - classEntity: `@JoinColumn(name = "class_id")` ✅ (Critical - was causing `class_entity_id` error)
   - section: `@JoinColumn(name = "section_id")`

2. **Section.java**
   - school: `@JoinColumn(name = "school_id")`
   - classEntity: `@JoinColumn(name = "class_id")` ✅

3. **ClassEntity.java**
   - school: `@JoinColumn(name = "school_id")` with FetchType.LAZY

4. **FeeStructure.java**
   - school: `@JoinColumn(name = "school_id")` ✓ (Already correct)
   - classEntity: `@JoinColumn(name = "class_id")` ✓ (Already correct)

5. **StudentFee.java**
   - All relationships already properly annotated ✓

6. **FeePayment.java**
   - All relationships already properly annotated ✓

7. **Teacher.java**
   - school: `@JoinColumn(name = "school_id")`

8. **Subject.java**
   - school: `@JoinColumn(name = "school_id")`

9. **Exam.java**
   - school: `@JoinColumn(name = "school_id")`

10. **Marks.java**
    - school: `@JoinColumn(name = "school_id")`
    - student: `@JoinColumn(name = "student_id")`
    - exam: `@JoinColumn(name = "exam_id")`
    - subject: `@JoinColumn(name = "subject_id")`

11. **Fee.java**
    - school: `@JoinColumn(name = "school_id")`
    - student: `@JoinColumn(name = "student_id")`

12. **Payment.java**
    - school: `@JoinColumn(name = "school_id")`
    - student: `@JoinColumn(name = "student_id")`

13. **StudentAttendance.java**
    - school: `@JoinColumn(name = "school_id")`
    - student: `@JoinColumn(name = "student_id")`

14. **TeacherAttendance.java**
    - school: `@JoinColumn(name = "school_id")`
    - teacher: `@JoinColumn(name = "teacher_id")`

15. **Salary.java**
    - school: `@JoinColumn(name = "school_id")`
    - teacher: `@JoinColumn(name = "teacher_id")`

16. **TeacherSubject.java**
    - teacher: `@JoinColumn(name = "teacher_id")`
    - classEntity: `@JoinColumn(name = "class_id")`
    - section: `@JoinColumn(name = "section_id")`
    - subject: `@JoinColumn(name = "subject_id")`

17. **Notification.java**
    - school: `@JoinColumn(name = "school_id")`

18. **AppUser.java**
    - school: `@JoinColumn(name = "school_id")`

## Performance Improvements
All `@ManyToOne` relationships now use:
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "columnName", nullable = false)
```

- **FetchType.LAZY**: Relationships are loaded only when explicitly accessed (improved N+1 query performance)
- **nullable = false**: Database integrity constraint prevents orphaned records

## Testing Results
✅ **API Test Passed**: `/api/schools/1/fees/student-fee` POST endpoint  
✅ **Database Column Error Fixed**: No more `class_entity_id` errors  
✅ **Server Startup Successful**: All entities properly mapped  

## Database Compatibility
All fixes align with the existing PostgreSQL schema:
- Column names: `school_id`, `student_id`, `class_id`, `section_id`, `parent_id`, `teacher_id`, etc.
- All foreign key constraints properly respected

## Deployment Notes
- No database migration required
- Only application code changes
- Backward compatible with existing data
- Recommended to test all endpoints using existing data before production deployment

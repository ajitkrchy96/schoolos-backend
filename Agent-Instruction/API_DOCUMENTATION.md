# SchoolOS Student Module - API Documentation

## Overview
The Student Module provides a complete REST API for managing students in the SchoolOS application. It includes operations for creating, reading, updating, and deleting students with comprehensive filtering, pagination, and validation.

## Base URL
```
/api/schools/{schoolId}/students
```

## Key Features
- ✅ Multi-school support with school_id validation
- ✅ Pagination and filtering
- ✅ Global exception handling
- ✅ Input validation with detailed error messages
- ✅ Soft delete functionality
- ✅ Search by name, admission number, and phone
- ✅ Filter by class and section
- ✅ Comprehensive logging

---

## API Endpoints

### 1. Create Student
**POST** `/api/schools/{schoolId}/students`

**Description:** Create a new student in the specified school.

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "dob": "2010-05-15",
  "admissionNo": "ADM001",
  "phone": "9876543210",
  "schoolId": 1,
  "classId": 1,
  "sectionId": 1,
  "parentId": 1,
  "status": "ACTIVE"
}
```

**Response:** HTTP 201 CREATED
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "dob": "2010-05-15",
  "admissionNo": "ADM001",
  "phone": "9876543210",
  "status": "ACTIVE",
  "schoolId": 1,
  "schoolName": "ABC School",
  "classId": 1,
  "className": "10-A",
  "sectionId": 1,
  "sectionName": "A",
  "parentId": 1,
  "parentName": "Mr. Doe Mrs. Doe",
  "createdAt": "2026-05-04T10:30:00",
  "updatedAt": "2026-05-04T10:30:00"
}
```

**Validation Rules:**
- `firstName`: Required, 2-50 characters
- `lastName`: Required, 2-50 characters
- `gender`: Required, must be "Male", "Female", or "Other"
- `dob`: Required, must be in the past
- `admissionNo`: Required, 3-20 characters
- `phone`: Required, exactly 10 digits
- `schoolId`: Required, must match path parameter
- `classId`: Required, must belong to the school
- `sectionId`: Required, must belong to the school
- `parentId`: Required, must belong to the school

---

### 2. Get Student by ID
**GET** `/api/schools/{schoolId}/students/{studentId}`

**Description:** Retrieve a specific student's details.

**Response:** HTTP 200 OK
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "gender": "Male",
  "dob": "2010-05-15",
  "admissionNo": "ADM001",
  "phone": "9876543210",
  "status": "ACTIVE",
  "schoolId": 1,
  "schoolName": "ABC School",
  "classId": 1,
  "className": "10-A",
  "sectionId": 1,
  "sectionName": "A",
  "parentId": 1,
  "parentName": "Mr. Doe Mrs. Doe",
  "createdAt": "2026-05-04T10:30:00",
  "updatedAt": "2026-05-04T10:30:00"
}
```

**Error:** HTTP 404 NOT_FOUND
```json
{
  "timestamp": "2026-05-04T10:35:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Student not found with id: '999'",
  "path": "/api/schools/1/students/999",
  "fieldErrors": null
}
```

---

### 3. Get All Students
**GET** `/api/schools/{schoolId}/students`

**Description:** Retrieve all students with pagination and optional filtering.

**Query Parameters:**
- `page` (default: 0): Page number (0-indexed)
- `size` (default: 20): Number of records per page
- `sort` (default: id): Sort field
- `direction` (default: DESC): Sort direction (ASC/DESC)
- `classId` (optional): Filter by class ID
- `sectionId` (optional): Filter by section ID
- `searchTerm` (optional): Search by name, admission number, or phone

**Response:** HTTP 200 OK
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "gender": "Male",
      "dob": "2010-05-15",
      "admissionNo": "ADM001",
      "phone": "9876543210",
      "status": "ACTIVE",
      "schoolId": 1,
      "schoolName": "ABC School",
      "classId": 1,
      "className": "10-A",
      "sectionId": 1,
      "sectionName": "A",
      "parentId": 1,
      "parentName": "Mr. Doe Mrs. Doe",
      "createdAt": "2026-05-04T10:30:00",
      "updatedAt": "2026-05-04T10:30:00"
    }
  ],
  "pageable": {
    "sort": {
      "empty": false,
      "sorted": true,
      "unsorted": false
    },
    "offset": 0,
    "pageSize": 20,
    "pageNumber": 0,
    "paged": true,
    "unpaged": false
  },
  "last": true,
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0,
  "sort": {
    "empty": false,
    "sorted": true,
    "unsorted": false
  },
  "numberOfElements": 1,
  "first": true,
  "empty": false
}
```

**Example Calls:**
```bash
# Get all students (default pagination)
GET /api/schools/1/students

# Get students by class and section
GET /api/schools/1/students?classId=1&sectionId=1

# Get students with custom pagination
GET /api/schools/1/students?page=0&size=50&sort=firstName&direction=ASC

# Get students with search term
GET /api/schools/1/students?searchTerm=John
```

---

### 4. Update Student
**PUT** `/api/schools/{schoolId}/students/{studentId}`

**Description:** Update an existing student's information.

**Request Body:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "gender": "Female",
  "dob": "2010-06-20",
  "admissionNo": "ADM001",
  "phone": "9876543211",
  "schoolId": 1,
  "classId": 1,
  "sectionId": 1,
  "parentId": 1,
  "status": "ACTIVE"
}
```

**Response:** HTTP 200 OK
```json
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Smith",
  "gender": "Female",
  "dob": "2010-06-20",
  "admissionNo": "ADM001",
  "phone": "9876543211",
  "status": "ACTIVE",
  "schoolId": 1,
  "schoolName": "ABC School",
  "classId": 1,
  "className": "10-A",
  "sectionId": 1,
  "sectionName": "A",
  "parentId": 1,
  "parentName": "Mr. Doe Mrs. Doe",
  "createdAt": "2026-05-04T10:30:00",
  "updatedAt": "2026-05-04T10:45:00"
}
```

---

### 5. Delete Student (Soft Delete)
**DELETE** `/api/schools/{schoolId}/students/{studentId}`

**Description:** Soft delete a student by setting status to INACTIVE.

**Response:** HTTP 204 NO_CONTENT

**Note:** This is a soft delete. The student record remains in the database but is marked as INACTIVE.

---

### 6. Search Students
**GET** `/api/schools/{schoolId}/students/search`

**Description:** Search students by term (name, admission number, or phone).

**Query Parameters:**
- `searchTerm` (required): Search keyword
- `page` (default: 0): Page number
- `size` (default: 20): Number of records per page
- `sort` (default: firstName): Sort field
- `direction` (default: ASC): Sort direction

**Response:** HTTP 200 OK
```json
{
  "content": [
    {
      "id": 1,
      "firstName": "John",
      "lastName": "Doe",
      "gender": "Male",
      "dob": "2010-05-15",
      "admissionNo": "ADM001",
      "phone": "9876543210",
      "status": "ACTIVE",
      "schoolId": 1,
      "schoolName": "ABC School",
      "classId": 1,
      "className": "10-A",
      "sectionId": 1,
      "sectionName": "A",
      "parentId": 1,
      "parentName": "Mr. Doe Mrs. Doe",
      "createdAt": "2026-05-04T10:30:00",
      "updatedAt": "2026-05-04T10:30:00"
    }
  ],
  "pageable": {...},
  "last": true,
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

**Example Calls:**
```bash
# Search by first name
GET /api/schools/1/students/search?searchTerm=John

# Search by admission number
GET /api/schools/1/students/search?searchTerm=ADM001

# Search by phone
GET /api/schools/1/students/search?searchTerm=9876543210
```

---

## Error Handling

### 1. Resource Not Found (404)
```json
{
  "timestamp": "2026-05-04T10:35:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Student not found with id: '999'",
  "path": "/api/schools/1/students/999",
  "fieldErrors": null
}
```

### 2. Validation Error (400)
```json
{
  "timestamp": "2026-05-04T10:40:00",
  "status": 400,
  "error": "VALIDATION_ERROR",
  "message": "Request validation failed",
  "path": "/api/schools/1/students",
  "fieldErrors": {
    "firstName": "First name is required",
    "phone": "Phone number must be exactly 10 digits"
  }
}
```

### 3. Unauthorized Access (403)
```json
{
  "timestamp": "2026-05-04T10:45:00",
  "status": 403,
  "error": "FORBIDDEN",
  "message": "Parent does not belong to the specified school",
  "path": "/api/schools/1/students",
  "fieldErrors": null
}
```

### 4. Internal Server Error (500)
```json
{
  "timestamp": "2026-05-04T10:50:00",
  "status": 500,
  "error": "INTERNAL_SERVER_ERROR",
  "message": "An unexpected error occurred. Please try again later.",
  "path": "/api/schools/1/students",
  "fieldErrors": null
}
```

---

## HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Successful GET/PUT request |
| 201 | Created | Successful POST request |
| 204 | No Content | Successful DELETE request |
| 400 | Bad Request | Validation error or invalid input |
| 403 | Forbidden | Unauthorized access (school mismatch) |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Unexpected server error |

---

## Architecture

### Package Structure
```
com.school
├── controller
│   └── student
│       └── StudentController.java
├── service
│   ├── student
│   │   └── StudentService.java
│   └── impl
│       └── StudentServiceImpl.java
├── dto
│   └── student
│       ├── StudentRequestDTO.java
│       ├── StudentResponseDTO.java
│       └── StudentFilterDTO.java
├── mapper
│   └── StudentMapper.java
├── repository
│   └── StudentRepository.java
├── exception
│   ├── ResourceNotFoundException.java
│   ├── ValidationException.java
│   └── UnauthorizedAccessException.java
└── config
    └── GlobalExceptionHandler.java
```

### Layer Responsibilities

1. **Controller**: Handles HTTP requests and responses
2. **Service**: Contains business logic and validation
3. **DTO**: Data transfer objects for request/response
4. **Mapper**: Converts between DTOs and entities
5. **Repository**: Database access layer
6. **Exception**: Custom exception classes
7. **Config**: Global configuration (exception handling)

---

## Key Features Implemented

### Multi-School Support
- Every operation is scoped by `schoolId`
- Ensures data isolation between schools
- Validates that related entities (parent, class, section) belong to the same school

### Validation
- Request validation using Jakarta validation annotations
- Custom validation for school ID consistency
- Phone number format validation
- Gender enum validation

### Pagination
- Configurable page size and sort order
- Default pagination: page 0, size 20
- Supports sorting by any field

### Filtering
- Filter by class ID
- Filter by section ID
- Search by name, admission number, or phone

### Soft Delete
- Students are not permanently deleted
- Status is set to "INACTIVE"
- Inactive students are excluded from default queries

### Logging
- Comprehensive logging with @Slf4j
- Logs important operations and errors
- Useful for debugging and auditing

---

## Development Notes

1. **Transaction Management**: All service methods use `@Transactional` for data consistency
2. **Lazy Loading**: Relationships use `FetchType.LAZY` to avoid N+1 queries
3. **Constructor Injection**: All dependencies injected via constructor for testability
4. **Immutability**: DTOs and responses are designed to be immutable
5. **Error Messages**: All error messages are meaningful and informative

---

## Future Enhancements

1. Add batch import/export for students
2. Add advanced search filters (date range, status, etc.)
3. Add integration with attendance module
4. Add file upload for student photos
5. Add audit logging for data changes
6. Add role-based access control (RBAC)
7. Add caching for frequently accessed data


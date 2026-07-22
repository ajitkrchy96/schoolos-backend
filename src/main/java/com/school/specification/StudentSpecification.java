package com.school.specification;

import com.school.dto.student.StudentFilterDTO;
import com.school.model.Student;
import com.school.utilenum.StudentStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public final class StudentSpecification {

    private StudentSpecification() {
    }

    public static Specification<Student> build(Long schoolId, StudentFilterDTO filterDTO) {
        return Specification.where(hasSchoolId(schoolId))
                .and(hasStatus(filterDTO.getStatus()))
                .and(hasClassId(filterDTO.getClassId()))
                .and(hasSectionId(filterDTO.getSectionId()))
                .and(hasSearchTerm(filterDTO.getSearchTerm()));
    }

    public static Specification<Student> hasSchoolId(Long schoolId) {
        return (root, query, builder) -> builder.equal(root.get("school").get("id"), schoolId);
    }

    public static Specification<Student> hasClassId(Long classId) {
        return (root, query, builder) -> classId == null
                ? builder.conjunction()
                : builder.equal(root.get("classEntity").get("id"), classId);
    }

    public static Specification<Student> hasSectionId(Long sectionId) {
        return (root, query, builder) -> sectionId == null
                ? builder.conjunction()
                : builder.equal(root.get("section").get("id"), sectionId);
    }

    public static Specification<Student> hasStatus(StudentStatus status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }

            Expression<StudentStatus> statusExpr = root.get("status");
            Predicate statusPredicate = builder.equal(statusExpr, status);

            if (status == StudentStatus.ACTIVE) {
                return builder.or(statusPredicate, builder.isNull(statusExpr));
            }

            return statusPredicate;
        };
    }

    public static Specification<Student> hasSearchTerm(String searchTerm) {
        return (root, query, builder) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return builder.conjunction();
            }

            String trimmed = searchTerm.trim();
            String likePatternLower = "%" + trimmed.toLowerCase() + "%";
            String likePatternRaw = "%" + trimmed + "%";

            Predicate firstName = builder.like(builder.lower(root.get("firstName")), likePatternLower);
            Predicate lastName = builder.like(builder.lower(root.get("lastName")), likePatternLower);
            Predicate admissionNo = builder.like(root.get("admissionNo"), likePatternRaw);
            Predicate phone = builder.like(root.get("phone"), likePatternRaw);
            return builder.or(firstName, lastName, admissionNo, phone);
        };
    }
}

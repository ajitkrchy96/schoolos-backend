package com.school.dto.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentFilterDTO {

    private Long classId;
    private Long sectionId;
    private String status;
    private String searchTerm;
}

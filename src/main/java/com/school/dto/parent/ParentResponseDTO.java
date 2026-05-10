package com.school.dto.parent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParentResponseDTO {

    private Long id;
    private String fatherName;
    private String motherName;
    private String phone;
    private String email;
    private String address;
}
package com.school.mapper;

import com.school.dto.parent.ParentRequestDTO;
import com.school.dto.parent.ParentResponseDTO;
import com.school.model.Parent;
import org.springframework.stereotype.Component;

@Component
public class ParentMapper {

    public Parent toEntity(ParentRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Parent parent = new Parent();
        parent.setFatherName(dto.getFatherName());
        parent.setMotherName(dto.getMotherName());
        parent.setPhone(dto.getPhone());
        parent.setEmail(dto.getEmail());
        parent.setAddress(dto.getAddress());

        return parent;
    }

    public ParentResponseDTO toResponseDTO(Parent parent) {
        if (parent == null) {
            return null;
        }

        return ParentResponseDTO.builder()
                .id(parent.getId())
                .fatherName(parent.getFatherName())
                .motherName(parent.getMotherName())
                .phone(parent.getPhone())
                .email(parent.getEmail())
                .address(parent.getAddress())
                .build();
    }

    public void updateEntityFromDTO(Parent parent, ParentRequestDTO dto) {
        if (parent == null || dto == null) {
            return;
        }

        parent.setFatherName(dto.getFatherName());
        parent.setMotherName(dto.getMotherName());
        parent.setPhone(dto.getPhone());
        parent.setEmail(dto.getEmail());
        parent.setAddress(dto.getAddress());
    }
}
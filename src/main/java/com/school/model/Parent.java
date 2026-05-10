package com.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parent")
@Getter @Setter
public class Parent extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    private String fatherName;
    private String motherName;
    private String phone;
    private String email;
    private String address;
}
package com.school.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "school")
@Getter @Setter
public class School extends BaseEntity {

    private String name;
    private String address;
    private String phone;
    private String email;
    private String subscriptionPlan;
}
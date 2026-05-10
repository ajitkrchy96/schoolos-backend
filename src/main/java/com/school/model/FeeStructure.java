package com.school.model;

import com.school.utilenum.FeeFrequency;
import jakarta.persistence.*;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "fee_structure")
@Getter
@Setter
public class FeeStructure extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private ClassEntity classEntity;

    private String name;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private FeeFrequency frequency;
    private Integer dueDay;
    @Column(nullable = false)
    private BigDecimal finePerDay = BigDecimal.ZERO;
}
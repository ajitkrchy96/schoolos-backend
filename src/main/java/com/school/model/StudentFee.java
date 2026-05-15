package com.school.model;

import com.school.utilenum.FeeStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_fee")
@Getter
@Setter
public class StudentFee extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private School school;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    @Column(nullable = false)
    private BigDecimal totalAmount;
    //TODO need to check both required or not totalAmount & totalFee
  //  @Column(nullable = false)
  //  private BigDecimal totalFee;

    @Column(nullable = false)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal dueAmount;

    @Enumerated(EnumType.STRING)
    private FeeStatus status;

    private LocalDate dueDate;
    private LocalDateTime lastPaymentDate;

    public void applyPayment(BigDecimal amount) {
        this.paidAmount = this.paidAmount.add(amount);
        this.dueAmount = this.totalAmount.subtract(this.paidAmount);

        if (this.dueAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = FeeStatus.PAID;
        } else if (this.paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.status = FeeStatus.PARTIAL;
        } else {
            this.status = FeeStatus.PENDING;
        }

        this.lastPaymentDate = LocalDateTime.now();
    }

}

package com.knowwhohow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Liabilities")
@Getter
@Setter
@NoArgsConstructor
public class Liabilities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "liability_id")
    private Long liabilityId;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "liability_type", nullable = false)
    private LiabilityType liabilityType; // Enum: LOAN

    @Column(name = "balance", nullable = false)
    private Integer balance;

    // --- 관계 매핑 ---
    // BankUser와의 N:1 관계 (필수)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BankUser bankUser;


    public enum LiabilityType {
        LOAN
    }
}

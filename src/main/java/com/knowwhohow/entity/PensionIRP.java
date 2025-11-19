package com.knowwhohow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Pension_IRP")
@Getter
@Setter
@NoArgsConstructor
public class PensionIRP {

    @Id
    @Column(name = "pension_id") // Pension의 PK를 공유
    private Long pensionId;

    @Column(name = "personal_contrib", precision = 15, scale = 2, nullable = false)
    private BigDecimal personalContrib;

    @Column(name = "contrib_year", nullable = false)
    private Integer contribYear;

    @Column(name = "total_personal_contrib", precision = 15, scale = 2, nullable = false)
    private BigDecimal totalPersonalContrib;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    // --- 관계 매핑 ---
    // Pension과의 1:1 관계 (필수). @MapsId를 사용하여 asset_id를 PK이자 FK로 사용
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "pension_id")
    private Pension pension;

    // ... Getters and Setters ...
}
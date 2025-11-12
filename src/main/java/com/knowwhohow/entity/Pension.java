package com.knowwhohow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Pension")
@Getter
@Setter
@NoArgsConstructor
public class Pension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pension_id")
    private Long pensionId;

    @Column(name = "account_name", nullable = false)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "pension_type", nullable = false)
    private PensionType pensionType; // Enum: DB, DC, IRP

    // --- 관계 매핑 ---
    // Asset과의 N:1 관계 (필수). @MapsId를 사용하여 asset_id를 PK이자 FK로 사용
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    // Pension_DC와의 1:1 관계 (선택적)
    @OneToOne(mappedBy = "pension", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PensionDC pensionDC;

    // Pension_IRP와의 1:1 관계 (선택적)
    @OneToOne(mappedBy = "pension", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private PensionIRP pensionIRP;


    public enum PensionType {
        DB, DC, IRP
    }
}

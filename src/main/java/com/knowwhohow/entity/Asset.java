package com.knowwhohow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Asset")
@Getter
@Setter
@NoArgsConstructor
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType; // Enum: CURRENT, SAVINGS, INVEST, PENSION

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "balance", nullable = false)
    private Integer balance;

    // --- 관계 매핑 ---
    // BankUser와의 N:1 관계 (필수). asset_id 필드는 user_id를 가리킴
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BankUser bankUser;

    // Pension과의 1:1 관계 (선택적). asset_id를 공유하는 관계
    @OneToMany(mappedBy = "asset", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Pension> pensions;


    public enum AssetType {
        CURRENT, SAVINGS, INVEST, PENSION
    }
}

package com.knowwhohow.entity;

import com.knowwhohow.config.EncryptConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "Bank_Users")
@Getter
@Setter
@NoArgsConstructor
public class BankUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false)
    @Convert(converter = EncryptConverter.class)
    private String userName;

    @Column(name = "user_age", nullable = false)
    private Integer userAge;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_gender", nullable = false)
    private Gender userGender; // Enum: F, M

    @Column(name = "user_code", nullable = false)
    @Convert(converter = EncryptConverter.class)
    private String userCode;

    // --- 관계 매핑 ---
    // 자산과의 1:N 관계. mappedBy는 Asset 클래스의 "bankUser" 필드를 참조
    @OneToMany(mappedBy = "bankUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Asset> assets;

    // 부채와의 1:N 관계. mappedBy는 Liabilities 클래스의 "bankUser" 필드를 참조
    @OneToMany(mappedBy = "bankUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Liabilities> liabilities;


    public enum Gender {
        F, M
    }
}
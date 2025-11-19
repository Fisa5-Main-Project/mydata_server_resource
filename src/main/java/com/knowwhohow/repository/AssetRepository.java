package com.knowwhohow.repository;

import com.knowwhohow.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    /**
     * 최적화 쿼리: Asset, Pension, PensionDC/IRP를 JOIN FETCH로 한 번에 로딩합니다.
     * (PensionIRP도 필요하다면 쿼리에 추가해야 합니다.)
     */
    @Query("SELECT a " +
            "FROM Asset a " +
            "JOIN FETCH a.bankUser u " + // BankUser 정보도 함께 로딩
            "LEFT JOIN FETCH a.pensions p " + // 1:N 관계인 Pension 로딩
            "LEFT JOIN FETCH p.pensionDC " + // 1:1 관계인 PensionDC 로딩
            "LEFT JOIN FETCH p.pensionIRP " + // 1:1 관계인 PensionIRP 로딩
            "WHERE u.userId = :userId")
    List<Asset> findAllByUserIdWithDetails(@Param("userId") Long userId);

    // 기존 findByBankUser_UserId는 이 쿼리로 대체하여 사용합니다.
}
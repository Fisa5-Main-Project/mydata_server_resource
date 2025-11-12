package com.knowwhohow.repository;

import com.knowwhohow.entity.Liabilities;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LiabilitiesRepository extends JpaRepository<Liabilities, Long> {

    // 1. 특정 유저가 가진 모든 부채 목록을 조회
    List<Liabilities> findByBankUser_UserId(Long userId);

    // 2. 특정 유저가 특정 liabilityId를 소유하고 있는지 확인
    Optional<Liabilities> findByLiabilityIdAndBankUser_UserId(Long liabilityId, Long userId);
}
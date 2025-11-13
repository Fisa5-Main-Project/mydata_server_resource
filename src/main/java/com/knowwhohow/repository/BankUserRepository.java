package com.knowwhohow.repository;

import com.knowwhohow.entity.BankUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankUserRepository extends JpaRepository<BankUser, Long> {

    Optional<BankUser> findByUserCode(String userCode);
}
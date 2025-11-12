package com.knowwhohow.controller;

import com.knowwhohow.dto.FinancialDataResponse;
import com.knowwhohow.service.FinancialDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/financial-data")
public class FinancialDataController {

    private final FinancialDataService financialDataService;

    public FinancialDataController(FinancialDataService financialDataService) {
        this.financialDataService = financialDataService;
    }

    // 💡 CI/access token에서 추출된 user_id를 가져오는 가상 메소드
    // 실제 Spring Security 통합 시 구현될 부분입니다.
    private Long getAuthenticatedUserId() {
        return 1L;
    }

    /**
     * GET /api/v1/financial-data : CI값에 맞는 사용자의 모든 자산/부채/연금 통합 조회 API
     * 시퀀스의 "검증 후 CI값에 맞는 데이터 반환" 단계에 해당합니다.
     */
    @GetMapping
    public ResponseEntity<FinancialDataResponse> getAllFinancialData() {
        // 1. 인증된 사용자 ID 추출
        Long authenticatedUserId = getAuthenticatedUserId();

        // 2. Service 호출: 이 단계에서 인가(소유권 검증)가 발생합니다.
        FinancialDataResponse allData = financialDataService.findAllFinancialData(authenticatedUserId);

        // 3. 통합 데이터 반환
        return ResponseEntity.ok(allData);
    }
}
package com.knowwhohow.controller;

import com.knowwhohow.dto.FinancialDataResponse;
import com.knowwhohow.global.dto.ApiResponse;
import com.knowwhohow.service.FinancialDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1")
public class FinancialDataController {

    private final FinancialDataService financialDataService;

    public FinancialDataController(FinancialDataService financialDataService) {
        this.financialDataService = financialDataService;
    }


    /**
     * GET /api/v1/financial-data : CI값에 맞는 사용자의 모든 자산/부채/연금 통합 조회 API
     * 시퀀스의 "검증 후 CI값에 맞는 데이터 반환" 단계에 해당
     * 인가: @AuthenticationPrincipal로 SecurityContext의 user_id를 주입받아 소유권 검증에 사용
     */
    @GetMapping("/my-data")
    public ResponseEntity<ApiResponse<FinancialDataResponse>> getAllFinancialData(
            @AuthenticationPrincipal Long authenticatedUserId
    ) {
        // 1. Service 호출: 인가된 user_id를 전달하여 소유권 검증 및 데이터 조회
        FinancialDataResponse data = financialDataService.findAllFinancialData(authenticatedUserId);

        // 2. ApiResponse로 감싸서 반환
        return ResponseEntity.ok(ApiResponse.onSuccess(data));
    }
}
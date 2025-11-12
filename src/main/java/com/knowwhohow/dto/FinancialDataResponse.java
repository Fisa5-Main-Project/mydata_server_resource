package com.knowwhohow.dto;

import com.knowwhohow.entity.Asset;
import com.knowwhohow.entity.Liabilities;
import java.util.List;

/**
 * CI에 맞는 사용자의 모든 금융 정보를 담는 최종 API 응답 객체
 */
public record FinancialDataResponse(
        List<AssetDTO> assets,
        List<LiabilitiesDTO> liabilities
) {
    // Entity List를 Response DTO로 변환하는 팩토리 메소드
    public static FinancialDataResponse fromEntities(List<Asset> assets, List<Liabilities> liabilities) {

        // Asset과 그 내부 연관관계(Pension)를 DTO로 변환
        List<AssetDTO> assetDTOs = assets.stream()
                .flatMap(AssetDTO::fromEntity) // 1:N 관계를 평탄화 (Flatten)
                .toList();

        // Liabilities를 DTO로 변환
        List<LiabilitiesDTO> liabilitiesDTOs = liabilities.stream()
                .map(LiabilitiesDTO::fromEntity)
                .toList();

        return new FinancialDataResponse(assetDTOs, liabilitiesDTOs);
    }
}
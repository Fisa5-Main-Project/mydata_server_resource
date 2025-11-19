package com.knowwhohow.dto;

import com.knowwhohow.entity.Asset;

import java.time.LocalDateTime;
import java.util.stream.Stream;

public record AssetDTO(
        String assetType,
        String bankCode,
        Integer balance,
        LocalDateTime updatedAt, // 메타데이터 추가
        PensionDTO pensionDetails // 단일 연금 상세 정보 (N개 중 1개)
) {

    // 💥 1:N 관계를 평탄화하기 위한 팩토리 메소드 (Stream 반환)
    public static Stream<AssetDTO> fromEntity(Asset asset) {

        // 1. assetType이 PENSION이 아닌 경우 (1:0 관계)
        if (asset.getAssetType() != Asset.AssetType.PENSION || asset.getPensions().isEmpty()) {
            return Stream.of(new AssetDTO(
                    asset.getAssetType().name(),
                    asset.getBankCode(),
                    asset.getBalance(),
                    LocalDateTime.now(), // 갱신일 임의 설정 (실제로는 엔티티에서 가져와야 함)
                    null // 연금 상세 정보 없음
            ));
        }

        // 2. assetType이 PENSION이며 1:N 관계인 경우 (N개 반환)
        return asset.getPensions().stream().map(pension -> {
            return new AssetDTO(
                    asset.getAssetType().name(),
                    asset.getBankCode(),
                    asset.getBalance(),
                    LocalDateTime.now(), // 갱신일 임의 설정
                    PensionDTO.fromEntity(pension)
            );
        });
    }
}
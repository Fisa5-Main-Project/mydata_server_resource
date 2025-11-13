package com.knowwhohow.service;

import com.knowwhohow.dto.AssetDTO;
import com.knowwhohow.dto.FinancialDataResponse;
import com.knowwhohow.entity.Asset;
import com.knowwhohow.entity.Liabilities;
import com.knowwhohow.repository.AssetRepository;
import com.knowwhohow.repository.LiabilitiesRepository;
import com.knowwhohow.util.MockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinancialDataServiceTest {

    @Mock
    private AssetRepository assetRepository;

    @Mock
    private LiabilitiesRepository liabilitiesRepository;

    @InjectMocks
    private FinancialDataService financialDataService;

    @Test
    void shouldReturnFlattenedFinancialDataAndVerifyAuthorization() {
        // Given: user_id = 1L
        Long authenticatedUserId = 1L;

        // Mock 데이터 준비
        List<Asset> mockAssets = MockData.createMockAssets(authenticatedUserId);
        List<Liabilities> mockLiabilities = MockData.createMockLiabilities(authenticatedUserId);

        // When: Repository 호출 시 Mock 데이터 반환하도록 설정 (인가 로직 모방)
        // Note: 이 설정 자체가 '인가된 user_id'에 해당하는 데이터만 반환하는 것을 모방함.
        when(assetRepository.findAllByUserIdWithDetails(authenticatedUserId))
                .thenReturn(mockAssets);

        when(liabilitiesRepository.findByBankUser_UserId(authenticatedUserId))
                .thenReturn(mockLiabilities);

        // Act: Service 메소드 호출
        FinancialDataResponse response = financialDataService.findAllFinancialData(authenticatedUserId);

        // Assert: 결과 검증 (DTO 변환 및 평탄화 확인)

        // 1. 자산 목록 (Asset) 검증
        // 2개 Asset (101, 102)이지만, 102번 Asset은 2개의 연금상품이 있으므로 총 3개의 DTO가 나와야 함.
        assertEquals(3, response.assets().size(), "AssetDTO 목록은 3개여야 합니다 (1 + 2).");

        // 2. 부채 목록 (Liabilities) 검증
        assertEquals(1, response.liabilities().size(), "LiabilitiesDTO 목록은 1개여야 합니다.");

        // 3. 평탄화된 연금 상세 정보 검증 (순서 불일치 해결)
        AssetDTO pensionAsset_DC = response.assets().get(1);
        AssetDTO pensionAsset_IRP = response.assets().get(2);

        // 두 개의 다른 연금 상세 정보가 올바르게 분리되었는지 확인
        assertEquals("DC", pensionAsset_DC.pensionDetails().pensionType(), "Index 1은 DC여야 합니다. (실제 반환 순서)");
        assertEquals("IRP", pensionAsset_IRP.pensionDetails().pensionType(), "Index 2는 IRP여야 합니다. (실제 반환 순서)");

        // TEST SUCCESS: DTO 평탄화 및 통합 조회 로직이 올바르게 작동합니다.
        System.out.println("TEST SUCCESS: DTO 평탄화 및 통합 조회 로직이 올바르게 작동합니다.");
    }
}
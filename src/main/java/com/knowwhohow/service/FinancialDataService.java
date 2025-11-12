package com.knowwhohow.service;

import com.knowwhohow.dto.FinancialDataResponse;
import com.knowwhohow.entity.Asset;
import com.knowwhohow.entity.Liabilities;
import com.knowwhohow.repository.AssetRepository;
import com.knowwhohow.repository.LiabilitiesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FinancialDataService {

    private final AssetRepository assetRepository;
    private final LiabilitiesRepository liabilitiesRepository;

    public FinancialDataService(AssetRepository assetRepository, LiabilitiesRepository liabilitiesRepository) {
        this.assetRepository = assetRepository;
        this.liabilitiesRepository = liabilitiesRepository;
    }

    /**
     * 💡 통합 인가 로직: CI(userId)에 맞는 사용자의 모든 금융 데이터를 조회합니다.
     * 인가 정책: Repository 레벨에서 userId를 기준으로 필터링하므로, 타인 데이터 접근이 차단됩니다.
     * @param authenticatedUserId 현재 인증된 사용자 ID (CI에 매핑된 내부 ID)
     * @return 모든 금융 정보 (자산, 부채)를 담은 Map
     */
    public FinancialDataResponse findAllFinancialData(Long authenticatedUserId) {

        // 1. 자산 (연금 상세 포함) 조회
        List<Asset> assets = assetRepository.findAllByUserIdWithDetails(authenticatedUserId);
        // 2. 부채 조회
        List<Liabilities> liabilities = liabilitiesRepository.findByBankUser_UserId(authenticatedUserId);

        // 연금 상세 정보는 Asset 엔티티의 pensions List 필드에 EAGER 또는 FETCH JOIN을 통해 이미 포함되어 있거나,
        // 필요에 따라 LAZY 로딩으로 접근 시 로딩될 수 있습니다. (구현의 편의를 위해 Asset에 포함된 것으로 간주합니다.)

        return FinancialDataResponse.fromEntities(assets, liabilities);
    }
}
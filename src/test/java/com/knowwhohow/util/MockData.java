package com.knowwhohow.util;

import com.knowwhohow.entity.*;

import java.math.BigDecimal;
import java.util.List;


public class MockData {

    public static List<Asset> createMockAssets(Long userId) {

        // 1. BankUser 객체 (연관관계용)
        BankUser user1 = new BankUser();
        user1.setUserId(userId);

        // 2. PensionDC 상세 데이터
        PensionDC dcDetail = new PensionDC();
        dcDetail.setCompanyContrib(new BigDecimal("3000000.00"));
        dcDetail.setPersonalContrib(new BigDecimal("1000000.00"));
        dcDetail.setContribYear(2025);
        dcDetail.setBalance(15000000);

        // 3. PensionIRP 상세 데이터
        PensionIRP irpDetail = new PensionIRP();
        irpDetail.setPersonalContrib(new BigDecimal("5000000.00"));
        irpDetail.setContribYear(2025);
        irpDetail.setTotalPersonalContrib(new BigDecimal("12000000.00"));
        irpDetail.setBalance(15000000);

        // 4. Pension 1 (DC형)
        Pension pensionDC = new Pension();
        pensionDC.setPensionId(301L);
        pensionDC.setAccountName("미래에셋 확정기여형");
        pensionDC.setPensionType(Pension.PensionType.DC);
        pensionDC.setPensionDC(dcDetail);
        dcDetail.setPension(pensionDC); // 양방향 매핑

        // 5. Pension 2 (IRP형)
        Pension pensionIRP = new Pension();
        pensionIRP.setPensionId(302L);
        pensionIRP.setAccountName("미래에셋 개인형IRP");
        pensionIRP.setPensionType(Pension.PensionType.IRP);
        pensionIRP.setPensionIRP(irpDetail);
        irpDetail.setPension(pensionIRP); // 양방향 매핑

        // 6. Asset A (저축성)
        Asset assetA = new Asset();
        assetA.setAssetId(101L);
        assetA.setBankUser(user1);
        assetA.setAssetType(Asset.AssetType.SAVINGS);
        assetA.setBankCode("088");
        assetA.setBalance(5000000);
        assetA.setAccountNumber("101-SAV-001");
        assetA.setPensions(List.of()); // 연금 없음

        // 7. Asset B (연금형 - 1:N 관계)
        Asset assetB = new Asset();
        assetB.setAssetId(102L);
        assetB.setBankUser(user1);
        assetB.setAssetType(Asset.AssetType.PENSION);
        assetB.setBankCode("263");
        assetB.setBalance(15000000);
        assetB.setAccountNumber("102-PEN-002");
        assetB.setPensions(List.of(pensionDC, pensionIRP)); // 2개의 연금 연결
        pensionDC.setAsset(assetB);
        pensionIRP.setAsset(assetB);

        return List.of(assetA, assetB);
    }

    public static List<Liabilities> createMockLiabilities(Long userId) {
        BankUser user1 = new BankUser();
        user1.setUserId(userId);

        Liabilities liabilityC = new Liabilities();
        liabilityC.setLiabilityId(501L);
        liabilityC.setBankUser(user1);
        liabilityC.setLiabilityType(Liabilities.LiabilityType.LOAN);
        liabilityC.setBankCode("020");
        liabilityC.setBalance(50000000);

        return List.of(liabilityC);
    }
}

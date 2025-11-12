package com.knowwhohow.dto;

import com.knowwhohow.entity.Liabilities;

public record LiabilitiesDTO(
        Long liabilityId,
        String bankCode,
        String liabilityType,
        Integer balance
) {
    public static LiabilitiesDTO fromEntity(Liabilities liabilities) {
        return new LiabilitiesDTO(
                liabilities.getLiabilityId(),
                liabilities.getBankCode(),
                liabilities.getLiabilityType().name(),
                liabilities.getBalance()
        );
    }
}

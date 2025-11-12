package com.knowwhohow.dto;

import com.knowwhohow.entity.PensionIRP;

import java.math.BigDecimal;

public record PensionIRPDTO(
        BigDecimal personalContrib,
        Integer contribYear,
        BigDecimal totalPersonalContrib,
        Integer balance
) {
    public static PensionIRPDTO fromEntity(PensionIRP pensionIRP) {
        return new PensionIRPDTO(
                pensionIRP.getPersonalContrib(),
                pensionIRP.getContribYear(),
                pensionIRP.getTotalPersonalContrib(),
                pensionIRP.getBalance()
        );
    }
}

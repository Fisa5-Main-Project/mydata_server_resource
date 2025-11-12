package com.knowwhohow.dto;

import com.knowwhohow.entity.PensionDC;

import java.math.BigDecimal;

public record PensionDCDTO(
        BigDecimal companyContrib,
        BigDecimal personalContrib,
        Integer contribYear,
        Integer balance
) {
    public static PensionDCDTO fromEntity(PensionDC pensionDC) {
        return new PensionDCDTO(
                pensionDC.getCompanyContrib(),
                pensionDC.getPersonalContrib(),
                pensionDC.getContribYear(),
                pensionDC.getBalance()
        );
    }
}
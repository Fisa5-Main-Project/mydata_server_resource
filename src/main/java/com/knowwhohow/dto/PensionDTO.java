package com.knowwhohow.dto;

import com.knowwhohow.entity.Pension;

import java.math.BigDecimal;
import java.util.Optional;

public record PensionDTO(
        String pensionType,
        String accountName,
        BigDecimal companyContrib,
        BigDecimal personalContrib,
        Integer contribYear,
        BigDecimal totalPersonalContrib
) {
    public static PensionDTO fromEntity(Pension pension) {

        PensionDCDTO dc = pension.getPensionDC() != null ? PensionDCDTO.fromEntity(pension.getPensionDC()) : null;
        PensionIRPDTO irp = pension.getPensionIRP() != null ? PensionIRPDTO.fromEntity(pension.getPensionIRP()) : null;

        // DC 또는 IRP 상세 정보를 기반으로 통합 필드를 채웁니다.
        BigDecimal finalCompanyContrib = Optional.ofNullable(dc).map(PensionDCDTO::companyContrib).orElse(BigDecimal.ZERO);
        BigDecimal finalPersonalContrib = Optional.ofNullable(dc).map(PensionDCDTO::personalContrib)
                .orElseGet(() -> Optional.ofNullable(irp).map(PensionIRPDTO::personalContrib).orElse(BigDecimal.ZERO));
        Integer finalContribYear = Optional.ofNullable(dc).map(PensionDCDTO::contribYear)
                .orElseGet(() -> Optional.ofNullable(irp).map(PensionIRPDTO::contribYear).orElse(null));
        BigDecimal finalTotalPersonalContrib = Optional.ofNullable(irp).map(PensionIRPDTO::totalPersonalContrib).orElse(BigDecimal.ZERO);

        return new PensionDTO(
                pension.getPensionType().name(),
                pension.getAccountName(),
                finalCompanyContrib,
                finalPersonalContrib,
                finalContribYear,
                finalTotalPersonalContrib
        );
    }
}

package com.example.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {
    private UUID groupId;
    private String groupName;
    private BigDecimal totalExpenses;
    private Integer service_charge_pct;
    private BigDecimal service_charge_amount;
    
    private List<DebtDetailResponse> debts;
}

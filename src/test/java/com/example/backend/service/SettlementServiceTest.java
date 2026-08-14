package com.example.backend.service;

import com.example.backend.dto.response.DebtDetailResponse;
import com.example.backend.dto.response.SettlementResponse;
import com.example.backend.entity.BillGroup;
import com.example.backend.entity.Expense;
import com.example.backend.entity.Participant;
import com.example.backend.repository.BillGroupRepository;
import com.example.backend.repository.ExpenseRepository;
import com.example.backend.repository.ParticipantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private BillGroupRepository groupRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private SettlementService settlementService;

    private BillGroup group;
    private Participant p1;
    private Participant p2;
    private Participant p3;
    
    private UUID groupId;
    private UUID p1Id;
    private UUID p2Id;
    private UUID p3Id;
    private UUID expenseId;

    @BeforeEach
    void setUp() {
        groupId = UUID.randomUUID();
        p1Id = UUID.randomUUID();
        p2Id = UUID.randomUUID();
        p3Id = UUID.randomUUID();
        expenseId = UUID.randomUUID();

        group = BillGroup.builder().id(groupId).name("Bali Trip").build();
        p1 = Participant.builder().id(p1Id).name("Andi").group(group).build();
        p2 = Participant.builder().id(p2Id).name("Budi").group(group).build();
        p3 = Participant.builder().id(p3Id).name("Citra").group(group).build();
    }

    @Test
    void testCalculateSettlement() {
        Expense expense = Expense.builder()
                .id(expenseId)
                .amount(new BigDecimal("300000.00"))
                .paidBy(p1)
                .group(group)
                .build();

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));
        when(participantRepository.findByGroupId(groupId)).thenReturn(Arrays.asList(p1, p2, p3));
        when(expenseRepository.findByGroupId(groupId)).thenReturn(Arrays.asList(expense));

        SettlementResponse response = settlementService.calculateSettlement(groupId);

        assertNotNull(response);
        assertEquals(new BigDecimal("300000.00"), response.getTotalExpenses());
        
        assertEquals(9, response.getService_charge_pct());
        
        assertEquals(new BigDecimal("27000.00"), response.getService_charge_amount());

        assertEquals(2, response.getDebts().size());

        BigDecimal totalDebt = BigDecimal.ZERO;
        for (DebtDetailResponse debt : response.getDebts()) {
            assertEquals("Andi", debt.getTo());
            assertEquals(new BigDecimal("100000.00"), debt.getAmount());
            totalDebt = totalDebt.add(debt.getAmount());
        }
        assertEquals(new BigDecimal("200000.00"), totalDebt);
    }
}

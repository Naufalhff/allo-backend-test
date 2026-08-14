package com.example.backend.service;

import com.example.backend.dto.response.DebtDetailResponse;
import com.example.backend.dto.response.SettlementResponse;
import com.example.backend.entity.BillGroup;
import com.example.backend.entity.Expense;
import com.example.backend.entity.Participant;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BillGroupRepository;
import com.example.backend.repository.ExpenseRepository;
import com.example.backend.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final BillGroupRepository groupRepository;
    private final ExpenseRepository expenseRepository;
    private final ParticipantRepository participantRepository;

    public SettlementResponse calculateSettlement(UUID groupId) {
        BillGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);
        List<Participant> participants = participantRepository.findByGroupId(groupId);

        if (participants.isEmpty()) {
            throw new IllegalArgumentException("Group has no participants");
        }

        BigDecimal totalExpenses = expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String githubUsername = "naufalhff";
        int asciiSum = 0;
        for (char c : githubUsername.toLowerCase().toCharArray()) {
            asciiSum += c;
        }
        int serviceChargePct = asciiSum % 10;
        BigDecimal serviceChargeAmount = totalExpenses
                .multiply(BigDecimal.valueOf(serviceChargePct))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal numParticipants = BigDecimal.valueOf(participants.size());
        BigDecimal sharePerPerson = totalExpenses.divide(numParticipants, 2, RoundingMode.HALF_UP);

        Map<Participant, BigDecimal> balances = new HashMap<>();
        for (Participant p : participants) {
            balances.put(p, sharePerPerson.negate()); 
        }

        for (Expense e : expenses) {
            Participant payer = e.getPaidBy();
            BigDecimal currentBalance = balances.get(payer);
            balances.put(payer, currentBalance.add(e.getAmount()));
        }

        List<Map.Entry<Participant, BigDecimal>> debtors = new ArrayList<>();
        List<Map.Entry<Participant, BigDecimal>> creditors = new ArrayList<>();

        for (Map.Entry<Participant, BigDecimal> entry : balances.entrySet()) {
            if (entry.getValue().compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(entry);
            } else if (entry.getValue().compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(entry);
            }
        }

        List<DebtDetailResponse> debts = new ArrayList<>();
        int i = 0, j = 0;

        while (i < debtors.size() && j < creditors.size()) {
            Map.Entry<Participant, BigDecimal> debtor = debtors.get(i);
            Map.Entry<Participant, BigDecimal> creditor = creditors.get(j);

            BigDecimal debtAmount = debtor.getValue().negate(); 
            BigDecimal creditAmount = creditor.getValue();

            BigDecimal minTransfer = debtAmount.min(creditAmount);

            debts.add(DebtDetailResponse.builder()
                    .from(debtor.getKey().getName())
                    .to(creditor.getKey().getName())
                    .amount(minTransfer)
                    .build());

            debtor.setValue(debtor.getValue().add(minTransfer));
            creditor.setValue(creditor.getValue().subtract(minTransfer));

            if (debtor.getValue().abs().compareTo(BigDecimal.valueOf(0.01)) < 0) {
                i++;
            }
            if (creditor.getValue().abs().compareTo(BigDecimal.valueOf(0.01)) < 0) {
                j++;
            }
        }

        return SettlementResponse.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .totalExpenses(totalExpenses)
                .service_charge_pct(serviceChargePct)
                .service_charge_amount(serviceChargeAmount)
                .debts(debts)
                .build();
    }
}

package com.example.backend.service;

import com.example.backend.dto.request.AddExpenseRequest;
import com.example.backend.dto.response.ExpenseResponse;
import com.example.backend.entity.BillGroup;
import com.example.backend.entity.Expense;
import com.example.backend.entity.Participant;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BillGroupRepository;
import com.example.backend.repository.ExpenseRepository;
import com.example.backend.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BillGroupRepository groupRepository;
    private final ParticipantRepository participantRepository;

    @Transactional
    public ExpenseResponse addExpense(UUID groupId, AddExpenseRequest request) {
        BillGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));

        Participant payer = participantRepository.findById(request.getPaidById())
                .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + request.getPaidById()));

        if (!payer.getGroup().getId().equals(groupId)) {
            throw new IllegalArgumentException("Participant does not belong to this group");
        }

        Expense expense = Expense.builder()
                .group(group)
                .description(request.getDescription())
                .amount(request.getAmount())
                .paidBy(payer)
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        return mapToResponse(savedExpense);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getGroupExpenses(UUID groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group not found with id: " + groupId);
        }

        return expenseRepository.findByGroupId(groupId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ExpenseResponse mapToResponse(Expense expense) {
        return ExpenseResponse.builder()
                .id(expense.getId())
                .description(expense.getDescription())
                .amount(expense.getAmount())
                .paidById(expense.getPaidBy().getId())
                .paidByName(expense.getPaidBy().getName())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}

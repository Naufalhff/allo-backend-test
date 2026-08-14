package com.example.backend.controller;

import com.example.backend.dto.WebResponse;
import com.example.backend.dto.request.AddExpenseRequest;
import com.example.backend.dto.response.ExpenseResponse;
import com.example.backend.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<ExpenseResponse> addExpense(@PathVariable UUID groupId, @Valid @RequestBody AddExpenseRequest request) {
        ExpenseResponse response = expenseService.addExpense(groupId, request);
        return new WebResponse<>("Expense added successfully", HttpStatus.CREATED.value(), response);
    }

    @GetMapping
    public WebResponse<List<ExpenseResponse>> getExpenses(@PathVariable UUID groupId) {
        List<ExpenseResponse> response = expenseService.getGroupExpenses(groupId);
        return new WebResponse<>("Expenses retrieved successfully", HttpStatus.OK.value(), response);
    }
}

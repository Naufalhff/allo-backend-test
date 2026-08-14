package com.example.backend.controller;

import com.example.backend.dto.WebResponse;
import com.example.backend.dto.response.SettlementResponse;
import com.example.backend.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups/{groupId}/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @GetMapping
    public WebResponse<SettlementResponse> getSettlement(@PathVariable UUID groupId) {
        SettlementResponse response = settlementService.calculateSettlement(groupId);
        return new WebResponse<>("Settlement calculated successfully", HttpStatus.OK.value(), response);
    }
}

package com.example.backend.controller;

import com.example.backend.dto.WebResponse;
import com.example.backend.dto.request.CreateGroupRequest;
import com.example.backend.dto.response.GroupResponse;
import com.example.backend.service.BillGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class BillGroupController {

    private final BillGroupService groupService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WebResponse<GroupResponse> createGroup(@Valid @RequestBody CreateGroupRequest request) {
        GroupResponse response = groupService.createGroup(request);
        return new WebResponse<>("Group created successfully", HttpStatus.CREATED.value(), response);
    }

    @GetMapping("/{id}")
    public WebResponse<GroupResponse> getGroup(@PathVariable UUID id) {
        GroupResponse response = groupService.getGroup(id);
        return new WebResponse<>("Group retrieved successfully", HttpStatus.OK.value(), response);
    }
}

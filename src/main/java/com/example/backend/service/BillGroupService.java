package com.example.backend.service;

import com.example.backend.dto.request.CreateGroupRequest;
import com.example.backend.dto.response.GroupResponse;
import com.example.backend.entity.BillGroup;
import com.example.backend.entity.Participant;
import com.example.backend.exception.ResourceNotFoundException;
import com.example.backend.repository.BillGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillGroupService {

    private final BillGroupRepository groupRepository;

    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {
        BillGroup group = BillGroup.builder()
                .name(request.getName())
                .build();

        request.getParticipants().forEach(pName -> {
            Participant participant = Participant.builder()
                    .name(pName)
                    .group(group)
                    .build();
            group.getParticipants().add(participant);
        });

        BillGroup savedGroup = groupRepository.save(group);
        return mapToResponse(savedGroup);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroup(UUID id) {
        BillGroup group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + id));
        return mapToResponse(group);
    }

    private GroupResponse mapToResponse(BillGroup group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .createdAt(group.getCreatedAt())
                .participants(group.getParticipants().stream()
                        .map(p -> GroupResponse.ParticipantResponse.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}

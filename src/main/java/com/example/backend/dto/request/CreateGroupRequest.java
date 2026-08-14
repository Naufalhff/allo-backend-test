package com.example.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateGroupRequest {
    @NotBlank(message = "Group name cannot be blank")
    private String name;

    @NotEmpty(message = "Group must have at least one participant")
    private List<@NotBlank(message = "Participant name cannot be blank") String> participants;
}

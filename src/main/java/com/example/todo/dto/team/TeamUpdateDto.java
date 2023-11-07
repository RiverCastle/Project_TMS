package com.example.todo.dto.team;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TeamUpdateDto {
    @NotBlank
    @Nullable
    private String name;
    @Nullable
    private String description;
    @NotBlank
    @Nullable
    private String joinCode;

    @Setter
    private List<String> usernamesOfManagers;
}

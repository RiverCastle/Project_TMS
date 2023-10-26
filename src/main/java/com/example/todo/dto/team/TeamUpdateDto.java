package com.example.todo.dto.team;

import com.example.todo.domain.entity.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class TeamUpdateDto {
    private String name;
    private String description;
    private String joinCode;
    private List<String> usernamesOfManagers;
}

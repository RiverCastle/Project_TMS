package com.example.todo.domain.entity;

import com.example.todo.domain.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @NotBlank
    @ManyToOne
    private TeamEntity team;

    @NotNull
    @ManyToOne
    private User user;

    private String role;
}

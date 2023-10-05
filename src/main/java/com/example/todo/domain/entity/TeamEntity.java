package com.example.todo.domain.entity;

import com.example.todo.domain.entity.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class TeamEntity extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String joinCode;
    @Nullable
    private Long motherId;
    @Nullable
    private Long belongsToId;
    private Integer participantNum;
    private Integer participantNumMax;
//
//    @Version
//    private Long version;

    @ManyToOne
    private User manager;

    @OneToMany(mappedBy = "team", fetch = FetchType.EAGER)
    private List<MemberEntity> members;

    public Long getManagerId() {
        return manager.getId();
    }
    public List<String> getMemebersNamesList(List<MemberEntity> members) {
        List<String> membersNamesList = new ArrayList<>();
        for (MemberEntity member : members) membersNamesList.add(member.getUser().getUsername());

        return membersNamesList;
    }
}

package com.example.todo.dto.team;

import com.example.todo.domain.entity.TeamEntity;
import com.example.todo.dto.task.TaskApiDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamDetailsDto {
    private String name;
    private List<String> managerNames;
    private String desc;
    private List<String> members;
    private Integer memberLimit;
    private List<TaskApiDto> notDoneTasks;
    private List<TaskApiDto> doneTasks;
    private List<TaskApiDto> allTasks;

    public static TeamDetailsDto fromEntity(TeamEntity teamEntity) {
        TeamDetailsDto teamDetailsDto = new TeamDetailsDto();
        teamDetailsDto.setName(teamEntity.getName());
//        teamDetailsDto.setManagerName(teamEntity.getManager().getUsername());
        teamDetailsDto.setDesc(teamEntity.getDescription());
//        teamDetailsDto.setMembers(teamEntity.getMemebersNamesList(teamEntity.getMembers()));
        teamDetailsDto.setMemberLimit(teamEntity.getParticipantNumMax());

        return teamDetailsDto;
    }
}

package com.example.todo.dto.team;

import com.example.todo.domain.entity.TeamEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TeamOverviewDto {
    private Long id;
    private String teamName;
    private String teamManagerName;
    private String teamDesc;

    public static TeamOverviewDto fromEntity(TeamEntity teamEntity) {
        TeamOverviewDto teamOverviewDto = new TeamOverviewDto();
        teamOverviewDto.setId(teamEntity.getId());
        teamOverviewDto.setTeamName(teamEntity.getName());
//        teamOverviewDto.setTeamManagerName(teamEntity.getManager().getUsername());
        teamOverviewDto.setTeamDesc(teamEntity.getDescription());
        return teamOverviewDto;
    }
}

package com.example.todo.dto.team;

import com.example.todo.domain.entity.TeamEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SubTeamOverviewDto {
    private Long id;
    private String teamName;
    private String teamManagerName;
    private String teamDesc;
    private Long belongsToId;

    public static SubTeamOverviewDto fromEntity(TeamEntity teamEntity) {
        SubTeamOverviewDto subTeamOverviewDto = new SubTeamOverviewDto();
        subTeamOverviewDto.setId(teamEntity.getId());
        subTeamOverviewDto.setTeamName(teamEntity.getName());
//        subTeamOverviewDto.setTeamManagerName(teamEntity.getManager().getUsername());
        subTeamOverviewDto.setTeamDesc(teamEntity.getDescription());
        subTeamOverviewDto.setBelongsToId(teamEntity.getBelongsToId());
        return subTeamOverviewDto;
    }
}

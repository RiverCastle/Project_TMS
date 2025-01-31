package com.example.todo.api.team;

import com.example.todo.dto.ResponseDto;
import com.example.todo.dto.team.*;
//import com.example.todo.facade.RedissonLockTeamFacade;
import com.example.todo.service.team.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;
//    private final RedissonLockTeamFacade redissonLockStockFacade;
    @GetMapping
    public List<TeamOverviewDto> searchTeam(@RequestParam(value = "keyword", defaultValue = "") String keyword) {
        return teamService.searchTeam(keyword);
    }

    @GetMapping("/{teamId}")
    public TeamDetailsDto getTeamDetails(Authentication authentication,
                                         @PathVariable("teamId") Long teamId) {
        Long userId = Long.parseLong(authentication.getName());
        return teamService.getTeamDetails(userId, teamId);
    }

    @PostMapping
    public TeamOverviewDto createTeam(Authentication authentication,
                                  @RequestBody TeamCreateDto teamCreateDto) {
        Long userId = Long.parseLong(authentication.getName());
        TeamOverviewDto teamOverviewDto = teamService.createTeam(userId, teamCreateDto);
        return teamOverviewDto;
    }

    @GetMapping("/{teamId}/subTeam")
    public List<SubTeamOverviewDto> searchSubTeam(Authentication authentication,
                                                  @PathVariable("teamId") Long teamId) {
        Long userId = Long.parseLong(authentication.getName());
        return teamService.searchSubTeams(userId, teamId);
    }

    @PostMapping("/{teamId}/subTeam")
    public SubTeamOverviewDto createSubTeam(Authentication authentication,
                                     @PathVariable("teamId") Long teamId,
                                     @RequestBody TeamCreateDto teamCreateDto) {
        Long userId = Long.parseLong(authentication.getName());
        SubTeamOverviewDto subTeamOverviewDto = teamService.createSubTeam(userId, teamId, teamCreateDto);

        return subTeamOverviewDto;
    }

    @PostMapping("/{teamId}/member")
    public ResponseDto joinTeam(Authentication authentication,
                                @RequestBody @Valid TeamJoinDto teamJoinDto,
                                @PathVariable("teamId") Long teamId) throws InterruptedException {
        Long userId = Long.parseLong(authentication.getName());
        teamService.joinTeam(userId, teamJoinDto, teamId);
//        redissonLockStockFacade.joinTeam(userId, teamJoinDto, teamId);
        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("팀에 가입이 완료되었습니다.");
        return responseDto;
    }


    @PutMapping("/{teamId}")
    public ResponseDto updateTeamDetails(Authentication authentication,
                                         @RequestBody TeamUpdateDto teamUpdateDto,
                                         @PathVariable("teamId") Long teamId) {
        Long userId = Long.parseLong(authentication.getName());
        teamService.updateTeamDetails(userId, teamUpdateDto, teamId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("팀 정보 수정이 완료되었습니다.");
        return responseDto;
    }


    @DeleteMapping("/{teamId}")
    public ResponseDto deleteTeam(Authentication authentication,
                                  @PathVariable("teamId") Long teamId) {
        Long userId = Long.parseLong(authentication.getName());
        teamService.deleteTeam(userId, teamId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("팀 삭제가 완료되었습니다.");
        return responseDto;
    }


    @DeleteMapping("/{teamId}/member")
    public ResponseDto leaveTeam(Authentication authentication,
                                 @PathVariable("teamId") Long teamId) {
        Long userId = Long.parseLong(authentication.getName());
        teamService.leaveTeam(userId, teamId);
//        redissonLockStockFacade.leaveTeam(userId, teamId);

        ResponseDto responseDto = new ResponseDto();
        responseDto.setMessage("팀을 탈퇴하였습니다.");
        return responseDto;
    }
}

package com.example.todo.service.team;

import com.example.todo.domain.entity.MemberEntity;
import com.example.todo.domain.entity.TeamEntity;
import com.example.todo.domain.entity.UsersSubscriptionEntity;
import com.example.todo.domain.entity.enums.SubscriptionStatus;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.MemberRepository;
import com.example.todo.domain.repository.TeamReposiotry;
import com.example.todo.domain.repository.UsersSubscriptionRepository;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.task.TaskApiDto;
import com.example.todo.dto.team.*;
import com.example.todo.exception.ErrorCode;
import com.example.todo.exception.TodoAppException;
import com.example.todo.service.task.TaskApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamReposiotry teamReposiotry;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final TaskApiService taskApiService;
    private final UsersSubscriptionRepository usersSubscriptionRepository;
    public static final int FREE_TEAM_PARTICIPANT_NUM = 25;
    @Transactional
    public void createTeam(Long userId, TeamCreateDto teamCreateDto) {
        User manager = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));

        //팀 최대인원이 5명을 초과할 시 구독권을 구독해야 한다.
        if (teamCreateDto.getParticipantNumMax() > FREE_TEAM_PARTICIPANT_NUM) {
            UsersSubscriptionEntity usersSubscription = usersSubscriptionRepository.findByUsersAndSubscriptionStatus(manager, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_ACTIVE_SUBSCRIPTION));
            if (teamCreateDto.getParticipantNumMax() > usersSubscription.getSubscription().getMaxMember())
                throw new TodoAppException(ErrorCode.EXCEED_ALLOWED_TEAM_MEMBERS);
        }

        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setName(teamCreateDto.getName());
        teamEntity.setDescription(teamCreateDto.getDescription());
        teamEntity.setJoinCode(teamCreateDto.getJoinCode());
        teamEntity.setManager(manager);
        teamEntity.setParticipantNumMax(teamCreateDto.getParticipantNumMax());

        // manager를 멤버로 추가
        MemberEntity member = new MemberEntity();
        member.setTeam(teamEntity);
        member.setUser(manager);

        teamEntity.setMembers(new ArrayList<>());
        teamEntity.getMembers().add(member);
        teamEntity.setParticipantNum(teamEntity.getMembers().size());
        teamReposiotry.save(teamEntity);
        memberRepository.save(member);
    }

    @Transactional
    public void joinTeam(Long userId, TeamJoinDto teamJoinDto, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));

        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
//        TeamEntity team = teamReposiotry.findByIdWithPessimisticLock(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
//        TeamEntity team = teamReposiotry.findByIdWithOptimisticLock(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));

        if (!team.getJoinCode().equals(teamJoinDto.getJoinCode()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong JoinCode!");

        //팀 멤버수 제한
        if (team.getParticipantNum().equals(team.getParticipantNumMax()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀의 최대 허용 멤버 수를 초과했습니다.");


        if (memberRepository.findByTeamAndUser(team, user).isPresent())
            throw new TodoAppException(ErrorCode.ALREADY_USER_JOINED);

        MemberEntity member = new MemberEntity();
        member.setTeam(team);
        member.setUser(user);
        memberRepository.save(member);

        team.getMembers().add(member);
        team.setParticipantNum(team.getParticipantNum() + 1);
        teamReposiotry.save(team);

    }

    public void updateTeamDetails(Long userId, TeamUpdateDto teamUpdateDto, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));

        if (team.getManagerId() != user.getId()) throw new TodoAppException(ErrorCode.MISMATCH_MANAGERID_USERID);


        if (!teamUpdateDto.getName().equals(""))
            team.setName(teamUpdateDto.getName());


        if (!teamUpdateDto.getDescription().equals(""))
            team.setDescription(teamUpdateDto.getDescription());


        if (!teamUpdateDto.getJoinCode().equals(""))
            team.setJoinCode(teamUpdateDto.getJoinCode());


        if (!teamUpdateDto.getManager().getUsername().equals("")) {
            MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
            team.setManager(member.getUser());
        }

        teamReposiotry.save(team);
    }

    public void deleteTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));

        if (!Objects.equals(team.getManagerId(), user.getId())) throw new TodoAppException(ErrorCode.MISMATCH_MANAGERID_USERID);

        teamReposiotry.delete(team);
    }

    @Transactional
    public void leaveTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        member.setTeam(null);
        memberRepository.delete(member);
        team.getMembers().remove(member);
        log.info("part {}", team.getParticipantNum() - 1);
        team.setParticipantNum(team.getParticipantNum() - 1);
        teamReposiotry.save(team);
    }


    public List<TeamOverviewDto> searchTeam(String keyword) {
        List<TeamEntity> teamEntityList = teamReposiotry.findAllByNameContainingAndDeletedAtIsNullAndBelongsToIdIsNull(keyword);
        List<TeamOverviewDto> teamOverviewDtoList = new ArrayList<>();
        for (TeamEntity teamEntity: teamEntityList) teamOverviewDtoList.add(TeamOverviewDto.fromEntity(teamEntity));

        return teamOverviewDtoList;
    }

    public List<SubTeamOverviewDto> searchSubTeams(Long userId, Long motherTeamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity motherTeam = teamReposiotry.findById(motherTeamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(motherTeam, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        List<TeamEntity> subTeamEntityList = teamReposiotry.findAllByMotherId(motherTeamId);
        List<SubTeamOverviewDto> subTeamOverviewDtoList = new ArrayList<>();
        for (TeamEntity subTeamEntity : subTeamEntityList) subTeamOverviewDtoList.add(SubTeamOverviewDto.fromEntity(subTeamEntity));

        return subTeamOverviewDtoList;
    }

    public TeamDetailsDto getTeamDetails(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        TeamDetailsDto teamDetailsDto = TeamDetailsDto.fromEntity(team);

        List<TaskApiDto> allTasksDtoList = taskApiService.readTasksAll(userId, teamId);
        for (TaskApiDto taskApiDto : allTasksDtoList) {
            teamDetailsDto.getAllTasks().add(taskApiDto);
            if (taskApiDto.getStatus().equals("DONE")) teamDetailsDto.getDoneTasks().add(taskApiDto);
            else teamDetailsDto.getNotDoneTasks().add(taskApiDto);
        }

        return teamDetailsDto;
    }

    public void createSubTeam(Long userId, Long teamId, TeamCreateDto teamCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity memberCheck = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        //팀 최대인원이 5명을 초과할 시 구독권을 구독해야 한다.
        if (teamCreateDto.getParticipantNumMax() > FREE_TEAM_PARTICIPANT_NUM) {
            UsersSubscriptionEntity usersSubscription = usersSubscriptionRepository.findByUsersAndSubscriptionStatus(user, SubscriptionStatus.ACTIVE)
                    .orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_ACTIVE_SUBSCRIPTION));
            if (teamCreateDto.getParticipantNumMax() > usersSubscription.getSubscription().getMaxMember())
                throw new TodoAppException(ErrorCode.EXCEED_ALLOWED_TEAM_MEMBERS);
        }

        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setName(teamCreateDto.getName());
        teamEntity.setDescription(teamCreateDto.getDescription());
        teamEntity.setJoinCode(teamCreateDto.getJoinCode());
        teamEntity.setManager(user);
        teamEntity.setParticipantNumMax(teamCreateDto.getParticipantNumMax());
        Long motherId = team.getMotherId() == null ? team.getId() : team.getMotherId();
        teamEntity.setMotherId(motherId); // 모조직 설정
        teamEntity.setBelongsToId(teamId); // 소속팀 설정

        // manager를 멤버로 추가
        MemberEntity member = new MemberEntity();
        member.setTeam(teamEntity);
        member.setUser(user);

        teamEntity.setMembers(new ArrayList<>());
        teamEntity.getMembers().add(member);
        teamEntity.setParticipantNum(teamEntity.getMembers().size());
        teamReposiotry.save(teamEntity);
        memberRepository.save(member);
    }
}

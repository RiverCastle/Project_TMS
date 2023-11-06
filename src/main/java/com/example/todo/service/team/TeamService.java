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

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public TeamOverviewDto createTeam(Long userId, TeamCreateDto teamCreateDto) {
        User manager = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));

        //팀 최대인원이 25명을 초과할 시 구독권을 구독해야 한다.
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
        teamEntity.setParticipantNum(1);
        teamEntity.setParticipantNumMax(teamCreateDto.getParticipantNumMax());

        // manager를 멤버로 추가
        MemberEntity member = new MemberEntity();
        member.setTeam(teamEntity);
        member.setUser(manager);
        member.setRole("Manager");
        teamEntity.setParticipantNum(1);

        teamEntity = teamReposiotry.save(teamEntity);
        memberRepository.save(member);

        return TeamOverviewDto.fromEntity(teamEntity);

    }

    @Transactional
    public void joinTeam(Long userId, TeamJoinDto teamJoinDto, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));

        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
//        TeamEntity team = teamReposiotry.findByIdWithPessimisticLock(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
//        TeamEntity team = teamReposiotry.findByIdWithOptimisticLock(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));

        if (!team.getJoinCode().equals(teamJoinDto.getJoinCode()))
            throw new TodoAppException(ErrorCode.BAD_JOINCODE);

        //팀 멤버수 제한
        if (team.getParticipantNum().equals(team.getParticipantNumMax()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "팀의 최대 허용 멤버 수를 초과했습니다.");
        log.info("OOOOOOOO");

        if (memberRepository.findByTeamAndUser(team, user).isPresent())
            throw new TodoAppException(ErrorCode.ALREADY_USER_JOINED);
        log.info("XXXX");
        MemberEntity member = new MemberEntity();
        member.setTeam(team);
        member.setUser(user);
        member.setRole("Member");
        memberRepository.save(member);

        team.setParticipantNum(team.getParticipantNum() + 1);
        teamReposiotry.save(team);
        log.info("XXXXXXXXXX");
    }

    public void updateTeamDetails(Long userId, TeamUpdateDto teamUpdateDto, Long teamId) {
        User managerUser = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity memberEntity = memberRepository.findByTeamAndUser(team, managerUser).orElseThrow(() -> new TodoAppException(ErrorCode.MISMATCH_MANAGERID_USERID));
        if (!memberEntity.getRole().equals("Manager")) throw new TodoAppException(ErrorCode.NOT_MATCH_MANAGERID);

        Optional<String> name = Optional.ofNullable(teamUpdateDto.getName());
        if (name.isPresent()) {
            String nameValue = name.get();
            if (nameValue.equals("")) throw new TodoAppException(ErrorCode.NOT_ALLOWED_EMPTY_TEAM_NAME);
            else team.setName(nameValue);
        }

        Optional<String> description = Optional.ofNullable(teamUpdateDto.getDescription());
        description.ifPresent(team::setDescription);
//                if (description.isPresent()) team.setDescription(description.get());

        Optional<String> joinCode = Optional.ofNullable(teamUpdateDto.getJoinCode());
        if (joinCode.isPresent()) {
            String joinCodeValue = joinCode.get();
            if (joinCodeValue.equals("")) throw new TodoAppException(ErrorCode.NOT_ALLOWED_EMPTY_JoinCode);
            else team.setJoinCode(joinCodeValue);
        }

        teamReposiotry.save(team);

        Optional<List<String>> optionalUsernamesList = Optional.ofNullable(teamUpdateDto.getUsernamesOfManagers());
        if (optionalUsernamesList.isPresent()) {
            List<String> usernamesToBeManager = teamUpdateDto.getUsernamesOfManagers();
            for (String username : usernamesToBeManager) {
                User userToBeManager = userRepository.findByUsername(username).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
                MemberEntity memberToBeManager = memberRepository.findByTeamAndUser(team, userToBeManager).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
                memberToBeManager.setRole("Manager");
                memberRepository.save(memberToBeManager);
            }
        }
    }

    public void deleteTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        String role = member.getRole();
        if (!role.equals("Manager")) throw new TodoAppException(ErrorCode.MISMATCH_MANAGERID_USERID);

        teamReposiotry.delete(team);
    }

    @Transactional
    public void leaveTeam(Long userId, Long teamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));
        if (member.getRole().equals("Manager")) throw new TodoAppException(ErrorCode.NOT_ALLOWED_LEAVE);

        member.setTeam(null);
        memberRepository.delete(member);
        log.info("part {}", team.getParticipantNum() - 1);
        team.setParticipantNum(team.getParticipantNum() - 1);
        teamReposiotry.save(team);
    }

    public List<TeamOverviewDto> searchTeam(String keyword) {
        List<TeamEntity> teamEntityList = teamReposiotry.findAllByNameContainingAndDeletedAtIsNullAndBelongsToIdIsNull(keyword);
        List<TeamOverviewDto> teamOverviewDtoList = new ArrayList<>();
        for (TeamEntity teamEntity: teamEntityList) {
            TeamOverviewDto teamOverviewDto = TeamOverviewDto.fromEntity(teamEntity);
            List<MemberEntity> managerMembers = memberRepository.findMemberEntitiesByTeamAndRole(teamEntity, "Manager");
            teamOverviewDto.setNumberOfManagers(managerMembers.size());
            teamOverviewDtoList.add(teamOverviewDto);
        }
        return teamOverviewDtoList;
    }

    public List<SubTeamOverviewDto> searchSubTeams(Long userId, Long motherTeamId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity motherTeam = teamReposiotry.findById(motherTeamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(motherTeam, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        List<TeamEntity> subTeamEntityList = teamReposiotry.findAllByMotherId(motherTeamId);
        List<SubTeamOverviewDto> subTeamOverviewDtoList = new ArrayList<>();
        for (TeamEntity subTeamEntity : subTeamEntityList) {
            SubTeamOverviewDto subTeamOverviewDto = SubTeamOverviewDto.fromEntity(subTeamEntity);
            MemberEntity managerMember = memberRepository.findMemberEntityByTeamAndAndRole(subTeamEntity, "Manager");
            subTeamOverviewDto.setTeamManagerName(managerMember.getUser().getUsername());
            subTeamOverviewDtoList.add(subTeamOverviewDto);
        }
        return subTeamOverviewDtoList;
    }

    public TeamDetailsDto getTeamDetails(Long userId, Long teamId) {
        log.info(userId + "  " + teamId);
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity member = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

        TeamDetailsDto teamDetailsDto = TeamDetailsDto.fromEntity(team);
        List<MemberEntity> managerMembers = memberRepository.findMemberEntitiesByTeamAndRole(team, "Manager");
        List<String> managerNames = new ArrayList<>();
        for (MemberEntity managerMember : managerMembers) managerNames.add(managerMember.getUser().getUsername());
        teamDetailsDto.setManagerNames(managerNames);

//        //TODO 분리하기
//        List<TaskApiDto> allTasksDtoList = taskApiService.readTasksAll(userId, teamId);
//        List<TaskApiDto> temp = new ArrayList<>();
//        for (TaskApiDto taskApiDto : allTasksDtoList) {
//            temp.add(taskApiDto);
//            if (taskApiDto.getStatus().equals("DONE")) teamDetailsDto.getDoneTasks().add(taskApiDto);
//            else teamDetailsDto.getNotDoneTasks().add(taskApiDto);
//        }

        return teamDetailsDto;
    }

    public SubTeamOverviewDto createSubTeam(Long userId, Long teamId, TeamCreateDto teamCreateDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_USER));
        TeamEntity team = teamReposiotry.findById(teamId).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_TEAM));
        MemberEntity supTeamMember = memberRepository.findByTeamAndUser(team, user).orElseThrow(() -> new TodoAppException(ErrorCode.NOT_FOUND_MEMBER));

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
        teamEntity.setParticipantNumMax(teamCreateDto.getParticipantNumMax());
        Long motherId = team.getMotherId() == null ? team.getId() : team.getMotherId();
        teamEntity.setMotherId(motherId); // 모조직 설정
        teamEntity.setBelongsToId(teamId); // 소속팀 설정
        teamEntity.setParticipantNum(1);

        // manager를 멤버로 추가
        MemberEntity member = new MemberEntity();
        member.setTeam(teamEntity);
        member.setUser(user);
        member.setRole("Manager");
        teamEntity = teamReposiotry.save(teamEntity);
        memberRepository.save(member);
        return SubTeamOverviewDto.fromEntity(teamEntity);
    }
}

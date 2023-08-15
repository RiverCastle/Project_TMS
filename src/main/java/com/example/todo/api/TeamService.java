package com.example.todo.api;

import com.example.todo.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamReposiotry teamReposiotry;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    public void createTeam(Authentication authentication, TeamCreateDto teamCreateDto) {
        long tempUserId = 4;
        Optional<User> optionalUser = userRepository.findById(tempUserId); //authentication 으로 대체
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저 존재 X");

        User manager = optionalUser.get();
        TeamEntity teamEntity = new TeamEntity();
        teamEntity.setName(teamCreateDto.getName());
        teamEntity.setDescription(teamCreateDto.getDescription());
        teamEntity.setJoinCode(teamCreateDto.getJoinCode());
        teamEntity.setManager(manager);
        teamReposiotry.save(teamEntity);

    }

    public void joinTeam(Authentication authentication, TeamJoinDto teamJoinDto, Long teamId) {
        long tempUserId = 4;
        Optional<User> optionalUser = userRepository.findById(tempUserId); //authentication 으로 대체
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저 존재 X");
        User user = optionalUser.get();

        Optional<TeamEntity> optionalTeamEntity = teamReposiotry.findById(teamId);
        if (optionalTeamEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀 존재 X");
        TeamEntity team = optionalTeamEntity.get();

        if (!team.getJoinCode().equals(teamJoinDto.getJoinCode())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong JoinCode!");

        MemberEntity member = new MemberEntity();
        member.setTeam(team);
        member.setUser(user);

        if (team.getMember().contains(member)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 해당 팀에 가입했음!");
        else team.getMember().add(member);
        teamReposiotry.save(team);

    }

    public void updateTeamDetails(Authentication authentication, TeamUpdateDto teamUpdateDto, Long teamId) {
        long tempUserId = 4;
        Optional<User> optionalUser = userRepository.findById(tempUserId); //authentication 으로 대체
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저 존재 X");
        User user = optionalUser.get();

        Optional<TeamEntity> optionalTeamEntity = teamReposiotry.findById(teamId);
        if (optionalTeamEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀 존재 X");
        TeamEntity team = optionalTeamEntity.get();

        if (team.getManager() != user) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현 관리자가 아닙니다.");

        if (teamUpdateDto.getName() != null) {
            team.setName(teamUpdateDto.getName());
        }

        if (teamUpdateDto.getDescription() != null) {
            team.setDescription(teamUpdateDto.getDescription());
        }

        if (teamUpdateDto.getJoinCode() != null) {
            team.setJoinCode(teamUpdateDto.getJoinCode());
        }

        if (teamUpdateDto.getManager() != null) {
            team.setManager(teamUpdateDto.getManager());
        }
        teamReposiotry.save(team);



    }

    public void deleteTeam(Authentication authentication, Long teamId) {
        long tempUserId = 4;
        Optional<User> optionalUser = userRepository.findById(tempUserId); //authentication 으로 대체
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저 존재 X");
        User user = optionalUser.get();

        Optional<TeamEntity> optionalTeamEntity = teamReposiotry.findById(teamId);
        if (optionalTeamEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀 존재 X");
        TeamEntity team = optionalTeamEntity.get();

        if (team.getManager() != user) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "현 관리자가 아닙니다.");

        teamReposiotry.delete(team);
    }

    public void leaveTeam(Authentication authentication, Long teamId) {
        long tempUserId = 4;
        Optional<User> optionalUser = userRepository.findById(tempUserId); //authentication 으로 대체
        if (optionalUser.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 유저 존재 X");
        User user = optionalUser.get();

        Optional<TeamEntity> optionalTeamEntity = teamReposiotry.findById(teamId);
        if (optionalTeamEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀 존재 X");
        TeamEntity team = optionalTeamEntity.get();


        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByTeamAndUser(team, user);
        if (optionalTeamEntity.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 팀에 가입X");
        MemberEntity member = optionalMemberEntity.get();

        team.getMember().remove(member);
        teamReposiotry.save(team);

        memberRepository.delete(member);
    }
}
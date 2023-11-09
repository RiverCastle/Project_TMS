package com.example.todo.service.team;

import com.example.todo.domain.entity.MemberEntity;
import com.example.todo.domain.entity.TeamEntity;
import com.example.todo.domain.entity.user.User;
import com.example.todo.domain.repository.MemberRepository;
import com.example.todo.domain.repository.TeamReposiotry;
import com.example.todo.domain.repository.user.UserRepository;
import com.example.todo.dto.team.*;
import com.example.todo.dto.user.request.UserJoinRequestDto;
import com.example.todo.dto.user.response.UserJoinResponseDto;
import com.example.todo.facade.OptimisticLockTeamFacade;
//import com.example.todo.facade.RedissonLockTeamFacade;
import com.example.todo.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class TeamServiceTest {

    @Autowired
    TeamService teamService;

    @Autowired
    UserService userService;

    @Autowired
    OptimisticLockTeamFacade optimisticLockTeamFacade;

//    @Autowired
//    RedissonLockTeamFacade redissonLockStockFacade;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamReposiotry teamReposiotry;

    @Autowired
    UserRepository userRepository;

    @DisplayName("새로 만들어진 팀에 회원 한 명이 가입하는 테스트")
    @Test
    void joinTeamWithOnePerson() {
        // given
        User user = createUser();
        createATeam(user.getId());

        User user1 = createUser();

        // when
        TeamJoinDto joinDto = TeamJoinDto.builder()
                .joinCode("참여코드")
                .build();
        teamService.joinTeam(user1.getId(), joinDto, 1L);
        List<MemberEntity> all = memberRepository.findAll();

        // then
        assertThat(all.size()).isEqualTo(2);
    }

    @DisplayName("Belongs to 컬럼이 null인 최상위 조직 조회 테스트")
    @Test
    void searchTeamTest() {
        // given
        int numberN = 10;
        // 검색시 조회되어야 할 이름이 Dep로 시작하는 최상위 조직 N + 1개
        createNTeamStartingWithDep(numberN);
        TeamCreateDto teamCreateDto = new TeamCreateDto("Dep 11", "Dep 팀 11 소개", "Dep 팀 11 참여코드", 5);
        TeamOverviewDto teamOverviewDto = teamService.createTeam(1l, teamCreateDto);
        // 검색시 조회되지 말아야 할 Dep로 시작하는 하위 조직 1개 생성
        // 하위팀 생성 전에 최상위 팀에 가입
        User user = createUser();
        TeamJoinDto teamJoinDto = new TeamJoinDto("Dep 팀 11 참여코드");
        teamService.joinTeam(user.getId(), teamJoinDto, teamOverviewDto.getId());
        // 하위팀 생성
        TeamCreateDto subteamCreateDto = new TeamCreateDto("Dep 서브팀 1", "소개 1", "참여코드", 6);
        teamService.createSubTeam(user.getId(), teamOverviewDto.getId(), subteamCreateDto);

        // when
        List<TeamOverviewDto> result11 = teamService.searchTeam("Dep");
        List<TeamOverviewDto> result0 = teamService.searchTeam("Commmmpa");

        // then
        assertThat(result11.size()).isEqualTo(11);
        assertThat(result0.size()).isEqualTo(0);
    }

    @DisplayName("팀 정보 업데이트 매니저 인원 추가 테스트")
    @Test
    void updateTeamManagerTest() {
        // given
        UserJoinRequestDto user1JoinReq = new UserJoinRequestDto("user1", "user1", "user1");
        UserJoinResponseDto user1 = userService.createUser(user1JoinReq); // 최초 매니저가 될 유저
        UserJoinRequestDto user2JoinReq = new UserJoinRequestDto("user2", "user2", "user2");
        UserJoinResponseDto user2 = userService.createUser(user2JoinReq); // 매니저로 추가될 유저
        TeamCreateDto testTeamCreateDto = new TeamCreateDto("test team", "test team desc", "test team joinCode", 10);
        TeamOverviewDto testTeamDto = teamService.createTeam(user1.getId(), testTeamCreateDto); // 유저 1이 팀 생성. 팀의 관리자가 된 상태.

        // 유저 2가 팀에 가입
        TeamJoinDto teamJoinDto = new TeamJoinDto("test team joinCode");
        teamService.joinTeam(user2.getId(), teamJoinDto, testTeamDto.getId());
        // 매니저로 추가
        TeamUpdateDto teamUpdateDto = new TeamUpdateDto();
        teamUpdateDto.setUsernamesOfManagers(new ArrayList<>());
        teamUpdateDto.getUsernamesOfManagers().add(user1.getUsername());
        teamUpdateDto.getUsernamesOfManagers().add(user2.getUsername());

        teamService.updateTeamDetails(user1.getId(), teamUpdateDto, testTeamDto.getId());

        // when
        TeamDetailsDto teamDetailsDto = teamService.getTeamDetails(user1.getId(), testTeamDto.getId());

        // then
        assertThat(teamDetailsDto.getManagerNames().size()).isEqualTo(2);
    }

//    @DisplayName("제한된 팀 숫자에 여러명이 동시에 가입하는 테스트")
//    @Test
//    void joinTeamWithRaceCondition() throws InterruptedException {
//        // 현재, 100번 동시에 요청이 된다고 가정하면 Member 엔티티 size 값이 5를 넘는 현상이 발생한다.
//        // 그리고 100번이 아니고 한 30번? 정도만 하면 Team 엔티티 participantNum 컬럼 값이 5가 아니다.
//        // given
//        User user = createUser();
//        createTeam(user.getId());
//        TeamJoinDto joinDto = TeamJoinDto.builder()
//                .joinCode("참여코드")
//                .build();
//
//        int threadCount = 100;
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        // when
//        for (int i = 0; i < threadCount; i++) {
//            User user1 = createUser();
////            long id = i + 3;
//            executorService.submit(() -> {
//                try {
//                    redissonLockStockFacade.joinTeam(user1.getId(), joinDto, 1L);
////                    optimisticLockTeamFacade.joinTeam(user1.getId(), joinDto, 1L);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        List<TeamEntity> all = teamReposiotry.findAll();
//        List<MemberEntity> members = memberRepository.findAll();
//        System.out.println("members.size() = " + members.size());
//
//        // then
//        //manager까지 101명
//        assertThat(all.get(0).getParticipantNum()).isEqualTo(101);
//        assertThat(members.size()).isEqualTo(101);
//
//        List<Long> list = new ArrayList<>();
//        for (MemberEntity member : members) {
//            list.add(member.getUser().getId());
//        }
//        Collections.sort(list);
//        for (Long l : list) {
//            System.out.println("l = " + l);
//        }
//    }

//    @DisplayName("제한된 팀 숫자에 여러명이 동시에 탈퇴하는 테스트")
//    @Test
//    void leaveTeamWithRaceCondition() throws InterruptedException {
//
//        // given
//        User user = createUser();
//        createTeam(user.getId());
//        TeamJoinDto joinDto = TeamJoinDto.builder()
//                .joinCode("참여코드")
//                .build();
//
//        int threadCount = 100;
//        //user 100명 생성 및 팀가입
//        for (int i = 0; i < threadCount; i++) {
//            User user1 = createUser();
//            redissonLockStockFacade.joinTeam(user1.getId(), joinDto, 1L);
//        }
//
//        //manager까지 101명 가입
//        List<User> users = userRepository.findAll();
//        List<TeamEntity> team = teamReposiotry.findAll();
//        List<MemberEntity> members = memberRepository.findAll();
//
//        int participantNumAfterTeamJoin = team.get(0).getParticipantNum();
//        int memberAfterTeamJoin = members.size();
//
//        ExecutorService executorService = Executors.newFixedThreadPool(32);
//        CountDownLatch latch = new CountDownLatch(threadCount);
//
//        List<Long> memberList = new ArrayList<>();
//        for (MemberEntity member : members){
//            memberList.add(member.getId());
//        }
//
//        for (Long userId : memberList){
//            if (userId.equals(user.getId())){
//                System.out.println("건너뜁니다");
//                continue;
//            }
//
//            executorService.submit(() -> {
//                try {
//                    redissonLockStockFacade.leaveTeam(userId, 1L);
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//
//        latch.await();
//
//        team = teamReposiotry.findAll();
//        members = memberRepository.findAll();
//
//        int participantNumAfterTeamLeave = team.get(0).getParticipantNum();
//        int memberAfterTeamLeave = members.size();
//
//
//        // then
//        assertThat(users.size()).isEqualTo(101);
//        assertThat(participantNumAfterTeamJoin).isEqualTo(101);
//        assertThat(memberAfterTeamJoin).isEqualTo(101);
//
//        //manager 제외하고 team leave
//        assertThat(participantNumAfterTeamLeave).isEqualTo(1);
//        assertThat(memberAfterTeamLeave).isEqualTo(1);
//        assertThat(members.get(0).getId()).isEqualTo(1L);
//
//    }

    // 유저 생성
    private User createUser() {
        return userRepository.saveAndFlush(User.builder()
                .username("test username")
                .password("test password")
                .build());
    }

    // 팀 1개 생성
    private void createATeam(Long userId) {
        TeamCreateDto createDto = TeamCreateDto.builder()
                .name("구매팀")
                .joinCode("참여코드")
                .participantNumMax(25)
                .description("구매팀입니다.")
                .build();
        teamService.createTeam(userId, createDto);
    }

    //팀 N개 생성
    private void createNTeamStartingWithDep(int numberN) {
        User user = createUser();
        for (int i = 1; i <= numberN; i++) {
            TeamCreateDto createDto = TeamCreateDto.builder()
                    .name("Dep " + i)
                    .joinCode("참여코드 " + i)
                    .description(i + "번 부서입니다.")
                    .participantNumMax(20)
                    .build();
            teamService.createTeam(user.getId(), createDto);
        }
    }
}
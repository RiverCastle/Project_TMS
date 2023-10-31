package com.example.todo.front;


import com.example.todo.api.team.TeamController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewController {
    private final TeamController teamController;
    @GetMapping("/main")
    public String main(Model model) {
        model.addAttribute("teamOverviewDtoList", teamController.searchTeam(""));
        log.info("메인페이지");
        return "main";
    }

    @GetMapping("/myTasks")
    public String myTasks(Model model) {
        log.info("내 업무 조회 페이지");
        return "myTasks";
    }

    @GetMapping("/login")
    public String login() {
        log.info("로그인페이지");
        return "login";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        log.info("회원가입페이지");
        return "sign-up";
    }

    @GetMapping("/team/{teamId}")
    public String teamPage(@PathVariable("teamId") Long teamId) {
        log.info("팀 조회 페이지");
        return "team-page";
    }

    @GetMapping("/team/{teamId}/tasks/{taskId}")
    public String taskPage(@PathVariable("teamId") Long teamId,
                           @PathVariable("taskId") Long taskId) {
        return "task-page";
    }
}

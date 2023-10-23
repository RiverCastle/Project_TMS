package com.example.todo.front;


import com.example.todo.api.team.TeamController;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/views")
@RequiredArgsConstructor
public class ViewController {
    private final TeamController teamController;
    @GetMapping("/main")
    public String main(Model model, Authentication authentication) {
        model.addAttribute("teamOverviewDtoList", teamController.searchTeam(""));
        System.out.println("메인으로 이동");
        if (authentication != null) System.out.println(authentication.getName() + "님 안녕하세요~!");
        else System.out.println("미인증-_-");
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/team/{teamId}")
    public String teamPage(Model model,
                           @PathVariable("teamId") Long teamId,
                           Authentication authentication) {
        model.addAttribute("teamDetails", teamController.getTeamDetails(authentication, teamId));
        return "team-page";
    }
}

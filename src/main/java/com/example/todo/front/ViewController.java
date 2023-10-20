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
    public String main(Model model) {
        model.addAttribute("teamOverviewDtoList", teamController.searchTeam(""));
        System.out.println("메인으로 이동");
        return "main";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/team/teamId")
    public String teamPage(Model model,
                           @PathVariable("teamId") Long teamId,
                           Authentication authentication) {
        model.addAttribute("teamDetails", teamController.getTeamDetails(authentication, teamId));
        return "team-page";
    }
}

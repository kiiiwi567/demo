package com.example.demo.controllers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(name = "showWarning", required = false, defaultValue = "false") boolean showWarning,
                        @RequestParam(name = "showEmailWarning", required = false, defaultValue = "false") boolean showEmailWarning,
                        @RequestParam(name = "showPasswordWarning", required = false, defaultValue = "false") boolean showPasswordWarning) {
        model.addAttribute("showWarning", showWarning);
        model.addAttribute("showEmailWarning", showEmailWarning);
        model.addAttribute("showPasswordWarning", showPasswordWarning);
        return "loginPage";
    }
}

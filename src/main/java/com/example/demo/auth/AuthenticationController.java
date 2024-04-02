package com.example.demo.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @GetMapping("/login")
    public RedirectView login(Model model,
                        @RequestParam(name = "showWarning", required = false, defaultValue = "false") boolean showWarning,
                        @RequestParam(name = "showEmailWarning", required = false, defaultValue = "false") boolean showEmailWarning,
                        @RequestParam(name = "showPasswordWarning", required = false, defaultValue = "false") boolean showPasswordWarning) {
        model.addAttribute("showWarning", showWarning);
        model.addAttribute("showEmailWarning", showEmailWarning);
        model.addAttribute("showPasswordWarning", showPasswordWarning);
        return new RedirectView("/login");
    }

    @PostMapping(value = "/authenticate", consumes = "application/x-www-form-urlencoded;charset=UTF-8")
    public RedirectView authenticate(@RequestParam("userEmail") String email,
                                     @RequestParam("password") String password,
                                     HttpServletResponse response) {
        return new RedirectView(service.authenticate(email, password, response));
    }

    @PostMapping(value="/logout")
    public RedirectView logout(HttpServletResponse response) {
        service.clearToken(response);
        return new RedirectView("/login");
    }
}

package com.fluxa.web;

import com.fluxa.security.FluxaUserDetailsService;
import com.fluxa.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MarketingController {

    private final UserService userService;
    private final FluxaUserDetailsService userDetailsService;

    @Value("${fluxa.demo.email}")
    private String demoEmail;

    public MarketingController(UserService userService, FluxaUserDetailsService userDetailsService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/")
    public String home() {
        return "marketing/landing";
    }

    @GetMapping("/pricing")
    public String pricing() {
        return "marketing/pricing";
    }

    @GetMapping("/features")
    public String features() {
        return "marketing/features";
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) model.addAttribute("error", "Invalid email or password");
        if (logout != null) model.addAttribute("message", "You have been signed out");
        return "marketing/login";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "marketing/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fullName,
                           @RequestParam String email,
                           @RequestParam String password,
                           @RequestParam(required = false) String companyName,
                           RedirectAttributes redirectAttributes) {
        try {
            userService.register(fullName, email, password, companyName);
            redirectAttributes.addFlashAttribute("message", "Account created. Please sign in.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/demo")
    public String demo(HttpServletRequest request) {
        UserDetails details = userDetailsService.loadUserByUsername(demoEmail);
        var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
        var context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        request.getSession(true).setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
        );
        return "redirect:/app";
    }
}

package com.example.servingwebcontent;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(@RequestParam(name="name", required=false, defaultValue="World") String name, Model model) {
        model.addAttribute("name", name);
        return "main";
    }
    @GetMapping("/login")
    public String login(HttpSession session,Model model) {
        if (session.getAttribute("user") == null) {
            return "login";
        }
        else
        {
            return "redirect:/dashboard";
        }
    }
    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }
    @GetMapping("/error")
    public String error(Model model) {
        return "error";
    }
}

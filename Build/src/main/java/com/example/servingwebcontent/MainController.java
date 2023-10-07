package com.example.servingwebcontent;

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
    public String login(Model model) {
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model) {
        return "register";
    }
}

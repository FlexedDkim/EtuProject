package com.example.servingwebcontent;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @GetMapping("/")
    public String main(HttpSession session,Model model) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        else
        {
            return "redirect:/dashboard";
        }
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
    public String register(HttpSession session,Model model) {
        if (session.getAttribute("user") == null) {
            return "register";
        }
        else
        {
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/restore")
    public String restore(HttpSession session,Model model) {
        if (session.getAttribute("user") == null) {
            return "restore";
        }
        else
        {
            return "redirect:/dashboard";
        }
    }

}

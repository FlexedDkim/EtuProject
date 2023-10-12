package com.example.servingwebcontent;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashBoardController {

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            String user = (String) session.getAttribute("user");
            model.addAttribute("user", user);
            return "lk";
        } else {
            return "redirect:/login";
        }
    }
}

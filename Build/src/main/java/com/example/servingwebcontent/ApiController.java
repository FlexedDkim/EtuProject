package com.example.servingwebcontent;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {
    String response = "false";

    @ResponseBody
    @PostMapping("/api/login")
    public String ApiLogin(HttpSession session,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        if (mail.equals("me@biarby.ru") && pass.equals("12345")) {
            session.setAttribute("user", mail);
            response = "Вход выполнен!";
        }
        else
        {
            response = "Ошибка в логине или пароле";
        }
        model.addAttribute("response", response);
        return response;
    }

    @GetMapping("/api/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}

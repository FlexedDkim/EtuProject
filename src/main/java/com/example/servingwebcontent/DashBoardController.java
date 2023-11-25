package com.example.servingwebcontent;

import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.managers.UserManager;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.servingwebcontent.func.GetHeaders;

import java.util.Optional;

@Controller
public class DashBoardController {
    UserManager userManager;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("user") != null) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(),"main",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/upload")
    public String dashboardUpload(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 1) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "uploads",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/edit")
    public String dashboardEdit(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 1) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "edit",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/comments")
    public String dashboardUserComments(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 1) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "comments",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }
    @GetMapping("/dashboard/settings")
    public String dashboardSettings(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() != 0) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "settings",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/newcomments")
    public String dashboardUserNewComments(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 2) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "newcomments",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/view")
    public String dashboardUserViewAndEdit(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 2) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "view",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/dashboard/createcard")
    public String dashboardUserCreateCard(HttpSession session, Model model) {
        if (session.getAttribute("user") != null && userManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() == 2) {
            String userSession = (String) session.getAttribute("user");
            Optional<User> userOptional = userManager.getUserByMail(userSession);
            User userDashBoard = userOptional.get();
            model.addAttribute("head", GetHeaders.GetHead(userDashBoard.getUsertype()));
            model.addAttribute("body", GetHeaders.GetBody(userDashBoard.getUsertype(), "createcard",userDashBoard.getId()));
            model.addAttribute("footer", GetHeaders.GetFooter());
            return "lk";
        } else {
            return "redirect:/login";
        }
    }
}

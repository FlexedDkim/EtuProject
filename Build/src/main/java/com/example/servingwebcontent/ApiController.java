package com.example.servingwebcontent;

import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.database.UserManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import static com.example.servingwebcontent.func.MainFunction.*;

@Controller
public class ApiController {
    @Autowired
    private UserManager userManager;
    String response = "false";
    String securepass = "";

    @ResponseBody
    @PostMapping("/api/login")
    public String ApiLogin(HttpSession session,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        Optional<User> userOptional = userManager.getUserByMail(mail);
        User isExemplary = null;
        if (userOptional.isPresent()) {
            isExemplary = userOptional.get();
        } else {
            response = "Ошибка в логине или пароле";
        }
        String salt = isExemplary.getSalt();
        String securepass = md5(salt + md5(salt + pass + salt));
        if (mail.equals(isExemplary.getMail()) && securepass.equals(isExemplary.getPass())) {
            session.setAttribute("user", mail);
            response = "Вход выполнен!";
        } else {
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

    @ResponseBody
    @PostMapping("/api/register")
    public String register(HttpSession session,@RequestParam(name="captcha", required=false) String captcha,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        long currentTimeMillis = System.currentTimeMillis();
        long unixTimestamp = currentTimeMillis / 1000;

        if (mail == "" || pass == "" || captcha == "") {
            return "Заполните все поля!";
        }

        if (!isValidEmail(mail)) {
            return "Почта невалидна!";
        }

        Optional<User> userOptional = UserManager.getUserByMail(mail);
        User user = userOptional.orElse(null);
        if (user != null) {
            return "Такая почта уже существует!";
        }

        if (!captcha.equals(session.getAttribute("captcha"))) {
            return "Неверный код с картинки!";
        }
        String salt = generateRandomString(10);
        securepass = md5( salt + md5(salt + pass + salt));
        User registerUser = new User();
        registerUser.setMail(mail);
        registerUser.setPass(securepass);
        registerUser.setTime(unixTimestamp);
        registerUser.setSalt(salt);
        registerUser.setDelete(false);
        UserManager.createUser(registerUser);
        session.setAttribute("user", mail);
        return "Аккаунт зарегистрирован!";
    }

    @GetMapping("/api/captcha")
    public void generateCaptcha(HttpServletResponse response,HttpSession session) throws IOException {
        int width = 200;
        int height = 50;
        int numberOfDigits = 5; // Количество цифр в капче

        // Генерация случайных чисел для капчи
        Random random = new Random();
        StringBuilder captchaNumber = new StringBuilder();

        for (int i = 0; i < numberOfDigits; i++) {
            int digit = random.nextInt(10); // случайное число от 0 до 9
            captchaNumber.append(digit);
        }

        // Сохранение полной цифровой капчи в переменной
        String captchaAnswer = captchaNumber.toString();

        // Создание изображения капчи
        BufferedImage captchaImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = captchaImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.PLAIN, 40));

        // Отображение каждой цифры на изображении с поворотом и искажением
        for (int i = 0; i < numberOfDigits; i++) {
            char digit = captchaNumber.charAt(i);

            // Поворот и искажение цифры
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(random.nextInt(15) - 7), i * 40 + 20, 30);
            transform.shear(random.nextDouble() - 0.5, random.nextDouble() - 0.5);
            Font transformedFont = g.getFont().deriveFont(transform);
            g.setFont(transformedFont);

            g.drawString(String.valueOf(digit), i * 40 + 20, 40);
        }
        session.setAttribute("captcha", captchaAnswer);
        response.setContentType("image/png");
        ImageIO.write(captchaImage, "png", response.getOutputStream());
        response.getOutputStream().close();
    }

}

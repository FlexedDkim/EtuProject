package com.example.servingwebcontent;

import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.database.File;
import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.managers.CardManager;
import com.example.servingwebcontent.managers.FileManager;
import com.example.servingwebcontent.managers.UserManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Random;

import java.util.List;

import static com.example.servingwebcontent.func.MainFunction.*;

@Controller
public class ApiController {
    @Autowired
    private UserManager userManager;
    @Autowired
    private FileManager fileManager;
    String response = "false";
    String securepass = "";

    @ResponseBody
    @PostMapping("/api/login")
    public String ApiLogin(HttpSession session,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        pass = pass.trim();
        mail = mail.trim().toLowerCase();

        if (mail == "" || pass == "") {
            return "Заполните все поля!";
        }

        Optional<User> userOptional = userManager.getUserByMail(mail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String salt = user.getSalt();
            String securePass = md5(salt + md5(salt + pass + salt));

            if (mail.equals(user.getMail()) && securePass.equals(user.getPass())) {
                session.setAttribute("user", mail);
                response = "Вход выполнен!";
            } else {
                response = "Пароль неверный!";
            }
        } else {
            response = "Такого логина не существует!";
        }
        return response;
    }

    @GetMapping("/api/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @ResponseBody
    @PostMapping("/api/register")
    public String register(HttpSession session,@RequestParam(name="pass_repeat", required=false) String pass_repeat,@RequestParam(name="captcha", required=false) String captcha,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        mail = mail.trim();
        pass = pass.trim();
        captcha = captcha.trim();
        pass_repeat = pass_repeat.trim();

        long unixTimestamp = getCurrentTime();

        mail = mail.toLowerCase();

        if (mail == "" || pass == "" || captcha == "" || pass_repeat == "") {
            return "Заполните все поля!";
        }

        if (!isValidEmail(mail)) {
            return "Почта невалидна!";
        }

        if (!pass_repeat.equals(pass)) {
            return "Введённые пароли не совпадают!";
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
    public void generateCaptcha(HttpServletResponse response, HttpSession session) throws IOException {
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
        g.setFont(new Font("Arial", Font.PLAIN, 40));

        // Отображение каждой цифры на изображении с поворотом и искажением
        for (int i = 0; i < numberOfDigits; i++) {
            char digit = captchaNumber.charAt(i);

            // Поворот и искажение цифры
            AffineTransform transform = new AffineTransform();
            transform.rotate(Math.toRadians(random.nextInt(3) - 1), i * 40 + 20, 20);
            transform.shear(random.nextDouble() - 0.5, random.nextDouble() - 0.5);
            Font transformedFont = g.getFont().deriveFont(transform);
            g.setFont(transformedFont);

            // Проверка, чтобы крайние цифры не выходили за рамки экрана
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(String.valueOf(digit));
            int xPosition = i * 40 + 20;
            if (xPosition + textWidth > width) {
                xPosition = width - textWidth - 10;
            }

            g.setColor(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256))); // Разноцветные цифры
            g.drawString(String.valueOf(digit), xPosition, 40);
        }

        // Генерация и добавление шумов на изображение
        for (int i = 0; i < 1500; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int rgb = random.nextInt(256); // случайный цвет
            captchaImage.setRGB(x, y, rgb);
        }

        session.setAttribute("captcha", captchaAnswer);
        response.setContentType("image/png");
        ImageIO.write(captchaImage, "png", response.getOutputStream());
        response.getOutputStream().close();
    }

    @RestController
    @RequestMapping("/api/uploadfiles")
    public class FileUploadController {
        private static final String UPLOAD_DIR = "src/main/resources/uploads/";
        @PostMapping
        public String handleFileUpload(@RequestParam("selectObject") Long selectObject,@RequestParam("files") List<MultipartFile> files,HttpSession session) {
            try {
                for (MultipartFile file : files) {
                    String checkfile = checkFile(file);
                    if (checkfile != "ok") {return checkfile;}
                }
                String mail = (String) session.getAttribute("user");
                Long idUser = userManager.getUserByMail(mail).get().getId();
                Long idCard = createCard(selectObject,idUser);
                for (MultipartFile file : files) {
                    saveFile(file,idCard,idUser);
                }
                return "Файлы были успешно загружены!";
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка при загрузки файлов";
            }
        }
        private void saveFile(MultipartFile file, Long idCard,Long idUser) throws IOException {
            long fileSize = file.getSize();
            String realFileName = file.getOriginalFilename();
            String genFileName = generateUniqueFileName();
            String fileExtension = getFileExtension(realFileName);
            Path filePath = Path.of(UPLOAD_DIR + genFileName + '.' + fileExtension);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            File newFile = new File();
            newFile.setRealName(realFileName);
            newFile.setGenName(genFileName);
            newFile.setTime(getCurrentTime());
            newFile.setType(fileExtension);
            newFile.setSize(fileSize);
            newFile.setIdOwn(idUser);
            newFile.setIdCard(idCard);
            newFile.setDeleted(false);
            FileManager.createFile(newFile);
        }

        private String checkFile(MultipartFile file) throws IOException {
            long fileSize = file.getSize();
            String realFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(realFileName);

            if (realFileName.length() > 30) {return "Файл " + realFileName + " имеет слишком длинное имя!";}
            if (canUploadFile(fileExtension) == false) {return "Тип файла " + fileExtension + " загружать нельзя!";}
            if (fileSize > 52428800) {return "Файл " + realFileName + " превышает допустимый размер.";}

            return "ok";
        }

        private Long createCard(Long selectObject, Long idOwn) {
            Card newCard = new Card();
            newCard.setName("Новая карточка");
            newCard.setTime(getCurrentTime());
            newCard.setIdObject(selectObject);
            newCard.setDeleted(false);
            newCard.setIdOwn(idOwn);
            CardManager.createCard(newCard);
            return CardManager.createCard(newCard).getId();
        }

    }


}

package com.example.servingwebcontent;

import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.database.Comment;
import com.example.servingwebcontent.database.File;
import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.managers.CardManager;
import com.example.servingwebcontent.managers.CommentManager;
import com.example.servingwebcontent.managers.FileManager;
import com.example.servingwebcontent.managers.UserManager;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import java.util.List;

import static com.example.servingwebcontent.func.MainFunction.*;
import static org.apache.logging.log4j.ThreadContext.isEmpty;

@Controller
public class ApiController {
    @Autowired
    private MailController mailController;
    @Autowired
    private UserManager userManager;
    @Autowired
    private FileManager fileManager;
    @Autowired
    private CardManager cardManager;
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
    public String register(HttpSession session,@RequestParam(name="passrepeat", required=false) String passrepeat,@RequestParam(name="captcha", required=false) String captcha,@RequestParam(name="mail", required=false) String mail, @RequestParam(name="pass", required=false) String pass, Model model) {
        mail = mail.trim();
        pass = pass.trim();
        captcha = captcha.trim();
        passrepeat = passrepeat.trim();

        long unixTimestamp = getCurrentTime();

        mail = mail.toLowerCase();

        if (mail == "" || pass == "" || captcha == "" || passrepeat == "") {
            return "Заполните все поля!";
        }

        if (!isValidEmail(mail)) {
            return "Почта невалидна!";
        }

        if (!passrepeat.equals(pass)) {
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
        registerUser.setUsertype(0);
        registerUser.setIdManager(0L);
        registerUser.setFname("");
        registerUser.setIname("");
        registerUser.setOname("");
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

    @ResponseBody
    @PostMapping("/api/createcomment")
    public Object ApiCreateComment(HttpSession session,@RequestParam(name="idcard", required=false) Long idCard, @RequestParam(name="idtext", required=false) String idText) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        class CommentResponse {
            @JsonProperty("status")
            private String status;
            @JsonProperty("message")
            private String message;
            @JsonProperty("date")
            private LocalDateTime date;
            @JsonProperty("fio")
            private String fio;
            @JsonProperty("count")
            private Long count;

            public void setStatus(String status) {
                this.status = status;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public void setDate(LocalDateTime date) {
                this.date = date;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public void setCount(Long count) {
                this.count = count;
            }

        }

        User userAuthor = userManager.getUserByMail((String) session.getAttribute("user")).get();

        Comment comment = new Comment();
        comment.setIdOwn(userAuthor.getId());
        comment.setIdCard(idCard);
        comment.setBody(idText);
        comment.setTime(getCurrentTime());
        comment.setDeleted(false);
        CommentManager.createComment(comment);

        Card cardComment = cardManager.readAllById(idCard).get();
        if (userAuthor.getUsertype() == 1) {cardComment.setStatus("open");} else {cardComment.setStatus("inwork");}
        CardManager.createCard(cardComment);

        CommentResponse response = new CommentResponse();
        response.setStatus("success");
        response.setMessage(idText.replace("\n", "<br>"));
        response.setDate(getInNormalDate(getCurrentTime()));
        response.setFio(userAuthor.getFname() + " " + userAuthor.getIname().charAt(0) + ". " + userAuthor.getOname().charAt(0) + ".");
        response.setCount(CommentManager.countByIdCard(idCard));

        return response;
    }

    @RestController
    @RequestMapping("/api/uploadfiles")
    public class FileUploadController {
        private static final String UPLOAD_DIR = "src/main/resources/uploads/";
        @PostMapping
        public String handleFileUpload(@RequestParam(name="desccard") String descCard,@RequestParam(name="namecard") String nameCard, @RequestParam("selectObject") Long selectObject,@RequestParam("files") List<MultipartFile> files,HttpSession session) {
            try {
                if (descCard.isEmpty() || nameCard.isEmpty()) {return "Заполните все поля!";}
                if (nameCard.length() > 30 || descCard.length() > 100) {return "Поле Имя карточки может содержать до 30 символов. Описание - до 100.";}
                for (MultipartFile file : files) {
                    String checkfile = checkFile(file);
                    if (checkfile != "ok") {return checkfile;}
                }
                String mail = (String) session.getAttribute("user");
                Long idUser = userManager.getUserByMail(mail).get().getId();
                Long idCard = createCard(selectObject,idUser,nameCard,descCard);
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

        private Long createCard(Long selectObject, Long idOwn, String nameCard, String descCard) {
            Card newCard = new Card();
            newCard.setName(nameCard);
            newCard.setTime(getCurrentTime());
            newCard.setIdObject(selectObject);
            newCard.setDeleted(false);
            newCard.setIdOwn(idOwn);
            newCard.setDescription(descCard);
            newCard.setStatus("open");
            CardManager.createCard(newCard);
            return CardManager.createCard(newCard).getId();
        }

    }

    @Controller
    @RequestMapping("/api/download")
    public class FileDownloadController {
        private final String uploadDirectory = "uploads/";

        @GetMapping("/{originalId:.+}")
        public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long originalId,HttpSession session) throws IOException {

            if (session.getAttribute("user") == null) {return ResponseEntity.notFound().build();}

            Optional<File> downloadFile = fileManager.readAllById(originalId);

            if (!downloadFile.isPresent()) {return ResponseEntity.notFound().build();}

            String RealFileName = downloadFile.get().getRealName();
            String originalFileName = downloadFile.get().getGenName();
            String filePath = uploadDirectory + originalFileName + "." + downloadFile.get().getType();

            ClassPathResource classPathResource = new ClassPathResource(filePath);

            if (!classPathResource.exists()) {
                return ResponseEntity.notFound().build();
            }

            Path tempFilePath = Files.createTempFile("tempFile-", RealFileName);

            Files.copy(classPathResource.getInputStream(), tempFilePath, StandardCopyOption.REPLACE_EXISTING);

            InputStream inputStream = Files.newInputStream(tempFilePath);
            InputStreamResource resource = new InputStreamResource(inputStream);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RealFileName);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
    }

    @ResponseBody
    @PostMapping("/api/newmailcreate")
    public String ApiRefreshGenMail(HttpSession session, @RequestParam(name="mail", required=false) String mail) {
        mail = mail.toLowerCase().trim();

        if (mail == "") {return "Заполните поле!";}

        Optional<User> userOptional = UserManager.getUserByMail(mail);
        User user = userOptional.orElse(null);
        if (user != null) {
            return "Такая почта уже существует!";
        }

        if (!isValidEmail(mail)) {
            return "Почта невалидна!";
        }

        String mailOld = (String) session.getAttribute("user");
        String code = generateRandomStringNum(5);
        session.setAttribute("gencodeCode", code);
        session.setAttribute("gencodeTime", getCurrentTime());
        session.setAttribute("gencodeMail", mail);
        session.setAttribute("gencodeAttempts", 0);

        mailController.sendEmail(mailOld,"Смена почты у вашего аккаунта.","Здравствуйте! Ваш код подтверждения для смены электронного ящика: " + code + " . Код действителен 5 минут.");

        return "ok";
    }

    @ResponseBody
    @PostMapping("/api/newmailcheck")
    public String ApiCheckGenMail(HttpSession session,@RequestParam(name="code", required=false) String code) {
        if ((int) session.getAttribute("gencodeAttempts") >= 3) {return "Вы исчерпали лимит попыток.";}
        Long oldTime = (Long) session.getAttribute("gencodeTime");
        Long newTime = getCurrentTime();
        if (newTime - oldTime > 300) {return "Время действия кода вышло.";}
        if (!code.equals((String) session.getAttribute("gencodeCode"))) {
            session.setAttribute("gencodeAttempts", (int) session.getAttribute("gencodeAttempts")+1);
            int Attempts = 3 - (int) session.getAttribute("gencodeAttempts");
            return "Код неверный! (Осталось " + Attempts + " попытки(а))";
        }
        else {
            String mail = (String) session.getAttribute("user");
            User updateUser = userManager.getUserByMail(mail).get();
            updateUser.setMail((String) session.getAttribute("gencodeMail"));
            session.setAttribute("user", (String) session.getAttribute("gencodeMail"));
            UserManager.createUser(updateUser);
            return "ok";
        }
    }

    @ResponseBody
    @PostMapping("/api/newpasscreate")
    public String ApiRefreshGenPassword(HttpSession session, @RequestParam(name="pass", required=false) String pass,@RequestParam(name="passrepeat", required=false) String passRepeat) {
        pass = pass.trim();
        passRepeat = passRepeat.trim();
        if (!passRepeat.equals(pass)) {return "Введённые пароли не совпадают!";}
        if (pass == "" || passRepeat == "") {return "Заполните поле!";}

        String mail = (String) session.getAttribute("user");
        String code = generateRandomStringNum(5);
        String salt = generateRandomString(10);
        securepass = md5( salt + md5(salt + pass + salt));

        session.setAttribute("gencodeCode", code);
        session.setAttribute("gencodeTime", getCurrentTime());
        session.setAttribute("gencodePass", securepass);
        session.setAttribute("gencodeSalt", salt);
        session.setAttribute("gencodeAttempts", 0);

        mailController.sendEmail(mail,"Смена пароля в вашем аккаунте.","Здравствуйте! Ваш код подтверждения для смены пароля: " + code + " . Код действителен 5 минут.");

        return "ok";
    }

    @ResponseBody
    @PostMapping("/api/newpasscheck")
    public String ApiCheckGenPassword(HttpSession session,@RequestParam(name="code", required=false) String code) {
        if ((int) session.getAttribute("gencodeAttempts") >= 3) {return "Вы исчерпали лимит попыток.";}
        Long oldTime = (Long) session.getAttribute("gencodeTime");
        Long newTime = getCurrentTime();
        if (newTime - oldTime > 300) {return "Время действия кода вышло.";}
        if (!code.equals((String) session.getAttribute("gencodeCode"))) {
            session.setAttribute("gencodeAttempts", (int) session.getAttribute("gencodeAttempts")+1);
            int Attempts = 3 - (int) session.getAttribute("gencodeAttempts");
            return "Код неверный! (Осталось " + Attempts + " попытки(а))";
        }
        else {
            String mail = (String) session.getAttribute("user");
            User updateUser = userManager.getUserByMail(mail).get();
            updateUser.setSalt((String) session.getAttribute("gencodeSalt"));
            updateUser.setPass((String) session.getAttribute("gencodePass"));
            UserManager.createUser(updateUser);
            return "ok";
        }
    }

}

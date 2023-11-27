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
import org.springframework.core.io.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @RestController
    @RequestMapping("/api/createcomment")
    public class CommentUploadController {
        private static final String UPLOAD_DIR = "src/main/resources/uploads/";
        @PostMapping
        public Object ApiCreateComment(HttpSession session, @RequestParam(name = "idcard", required = false) Long idCard, @RequestParam(name = "idtext", required = true) String idText, @RequestParam(name = "files", required = false) List<MultipartFile> files) {
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
                @JsonProperty("card")
                private String card;
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
                public void setCards(String card) {
                    this.card = card;
                }
            }
            if (idText.trim().equals("") || idText == null) {
                return false;
            }
            if (files != null) {
                for (MultipartFile file : files) {
                    String checkfile = null;
                    try {
                        checkfile = checkFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (checkfile != "ok") {
                        return checkfile;
                    }
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
            String tmpText = "";
            if (files != null) {
                for (MultipartFile file : files) {
                    try {
                        tmpText += createTmpCard(saveFile(file, idCard, userAuthor.getId()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            if (userAuthor.getUsertype() == 1) {
                cardComment.setStatus("open");
            } else {
                cardComment.setStatus("inwork");
            }
            CardManager.createCard(cardComment);
            CommentResponse response = new CommentResponse();
            response.setStatus("success");
            response.setMessage(idText.replace("\n", "<br>"));
            response.setDate(getInNormalDate(getCurrentTime()));
            response.setFio(userAuthor.getFname() + " " + userAuthor.getIname().charAt(0) + ". " + userAuthor.getOname().charAt(0) + ".");
            response.setCount(CommentManager.countByIdCard(idCard));
            response.setCards(tmpText);
            return response;
        }
        private File saveFile(MultipartFile file, Long idCard,Long idUser) throws IOException {
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
            return newFile;
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
        private String createTmpCard(File fileObj) throws IOException {
            String delBtn = "&nbsp;&nbsp;<button type=\"button\" onclick=\"deleteFile('"+fileObj.getId()+"')\" id=\"btnDeletedFile" + fileObj.getId() + "\" class=\"btn bg-danger text-light\">Удалить</button>";
            return "<div id=\"filecard"+fileObj.getId()+"\" style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                    "            <div class=\"card-body\">\n" +
                    "                <h5 id=\"namecard"+fileObj.getId()+"\" class=\"card-title\">" + fileObj.getRealName() + "</h5>\n" +
                    "                <p class=\"card-text\">" + convertFileSize(fileObj.getSize()) + "</p>\n" +
                    "                <button type=\"button\" onclick=\"window.open('/api/download/" + fileObj.getId() + "');\" id=\"btnDownloadFile" + fileObj.getId() + "\" class=\"btn bg-main text-light\">Скачать</button>" + delBtn +
                    "            </div>\n" +
                    "        </div>";
        }
    }
    @RestController
    @RequestMapping("/api/uploadfiles")
    public class FileUploadController {
        private static final String UPLOAD_DIR = "src/main/resources/uploads/";
        @PostMapping
        public String handleFileUpload(@RequestParam(name="desccard") String descCard,@RequestParam(name="namecard") String nameCard, @RequestParam("selectObject") Long selectObject,@RequestParam("files") List<MultipartFile> files,HttpSession session, @RequestParam("itemSelectEmployer") Long itemSelectEmployer) {
            try {
                if (itemSelectEmployer == null) {itemSelectEmployer = 0l;}
                if (descCard.isEmpty() || nameCard.isEmpty()) {return "Заполните все поля!";}
                if (nameCard.length() > 30 || descCard.length() > 100) {return "Поле Имя карточки может содержать до 30 символов. Описание - до 100.";}
                for (MultipartFile file : files) {
                    String checkfile = checkFile(file);
                    if (checkfile != "ok") {return checkfile;}
                }
                String mail = (String) session.getAttribute("user");
                Long idUser = userManager.getUserByMail(mail).get().getId();
                Long idCard = createCard(selectObject,idUser,nameCard,descCard,itemSelectEmployer);
                for (MultipartFile file : files) {
                    saveFile(file,idCard,idUser);
                }
                return "Файлы были успешно загружены!";
            } catch (IOException e) {
                e.printStackTrace();
                return "Ошибка при загрузке файлов";
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
        private Long createCard(Long selectObject, Long idOwn, String nameCard, String descCard,Long itemSelectEmployer) {
            Card newCard = new Card();
            newCard.setName(nameCard);
            newCard.setTime(getCurrentTime());
            newCard.setIdObject(selectObject);
            newCard.setDeleted(false);
            newCard.setIdOwn(idOwn);
            newCard.setDescription(descCard);
            if (userManager.getUserById(idOwn).get().getUsertype() == 1) {
                newCard.setStatus("open");
            } else {
                newCard.setStatus("inwork");
            }
            if (userManager.getUserById(idOwn).get().getUsertype() != 2) {newCard.setIdExecutor(idOwn);} else {newCard.setIdExecutor(itemSelectEmployer);}
            CardManager.createCard(newCard);
            return CardManager.createCard(newCard).getId();
        }
    }
    @Controller
    @RequestMapping("/api/download")
    public class FileDownloadController {

        private final Path uploadDirectory = Paths.get("src/main/resources/uploads/").toAbsolutePath().normalize();

        @GetMapping("/{originalId}")
        public ResponseEntity<Resource> downloadFile(@PathVariable Long originalId, HttpSession session) {
            if (session.getAttribute("user") == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Optional<File> downloadFile = fileManager.readAllById(originalId);
            if (!downloadFile.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            File fileInfo = downloadFile.get();
            String realFileName = fileInfo.getRealName();
            String generatedFileName = fileInfo.getGenName();
            String fileExtension = fileInfo.getType();

            Path filePath = uploadDirectory.resolve(generatedFileName + "." + fileExtension);

            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource;
            try {
                resource = new UrlResource(filePath.toUri());
                if (!resource.exists()) {
                    return ResponseEntity.notFound().build();
                }
            } catch (MalformedURLException e) {
                return ResponseEntity.internalServerError().build();
            }

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(realFileName, StandardCharsets.UTF_8);
            String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;
            headers.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }
    }
    @ResponseBody
    @PostMapping("/api/deletefile")
    public Object ApiDeleteFile(HttpSession session, @RequestParam(name="idfile", required=false) Long idfile) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        class DeleteResponse {
            @JsonProperty("status")
            private String status;
            @JsonProperty("name")
            private String name;
            public void setStatus(String status) {
                this.status = status;
            }
            public void setName(String message) {
                this.name = message;
            }
        }
        Long idUser = userManager.getUserByMail((String) session.getAttribute("user")).get().getId();
        Optional<File> userOptional = fileManager.readAllById(idfile);
        if (userOptional.isPresent()) {
            File fileDelete = userOptional.get();
            if (idUser == fileDelete.getIdOwn()) {
                DeleteResponse response = new DeleteResponse();
                response.setStatus("success");
                response.setName(fileDelete.getRealName() + " [Удалено]");
                String currentDir = System.getProperty("user.dir");
                String file = fileDelete.getGenName() + "." + fileDelete.getType();
                FileManager FileDelete = null;
                fileDelete.setDeleted(true);
                FileDelete.createFile(fileDelete);
                try {
                    Path path = Paths.get(Path.of(currentDir,"src","main","resources","uploads",file).toUri());
                    boolean isDeleted = Files.deleteIfExists(path);
                    if (isDeleted) {
                        System.out.println("Файл успешно удален: " + Path.of(currentDir,"src","main","resources","uploads",file).toUri());
                    } else {
                        System.out.println("Файл не существует: " + Path.of(currentDir,"src","main","resources","uploads",file).toUri());
                    }
                } catch (IOException e) {
                    System.err.println("Ошибка удаления файла: " + e.getMessage());
                }
                return response;
            }
        }
        else
        {
            DeleteResponse response = new DeleteResponse();
            response.setStatus("fail");
            response.setName("fail");
            return response;
        }
        return response;
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
    @ResponseBody
    @PostMapping("/api/searchengineuser")
    public Object SearchEngineUser(HttpSession session,@RequestParam(name="name", required=false) String name,@RequestParam(name="description", required=false) String description,@RequestParam(name="inputstatus", required=false) String status,@RequestParam(name="inputobject", required=false) Long object,@RequestParam(name="datestart", required=false) String datestart,@RequestParam(name="dateend", required=false) String dateend,@RequestParam(name="fnameauthorcard", required=false) String fnameauthorcard,@RequestParam(name="inameauthorcard", required=false) String inameauthorcard,@RequestParam(name="onameauthorcard", required=false) String onameauthorcard) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        class SearchResponse {
            @JsonProperty("status")
            private String status;
            @JsonProperty("messageup")
            private String messageUp;
            @JsonProperty("messagedown")
            private String messageDown;
            public void setStatus(String status) {
                this.status = status;
            }
            public void setMessageUp(String message) {
                this.messageUp = message;
            }
            public void setMessageDown(String message) {
                this.messageDown = message;
            }
        }
        List<Card> cards = CardManager.readAllByNameIgnoreCase(name.describeConstable(),description.describeConstable(),status.describeConstable(),object);
        String resp = "<table class=\"table\">\n" +
                "  <thead>\n" +
                "    <tr>\n" +
                "      <th scope=\"col\">#</th>\n" +
                "      <th scope=\"col\">Название</th>\n" +
                "      <th scope=\"col\">Описание</th>\n" +
                "      <th scope=\"col\">Редактирование</th>\n" +
                "    </tr>\n" +
                "  </thead>\n" +
                "  <tbody>";
        String respmodal = "";
        int counter = 0;
        String mail = (String) session.getAttribute("user");
        User searchUser = userManager.getUserByMail(mail).get();
        for (Card card : cards) {
            if (getBetwheenDates(datestart,dateend,card.getTime()) && (searchUser.getId() == card.getIdExecutor()) && userManager.searchUser(inameauthorcard,fnameauthorcard,onameauthorcard,searchUser.getId()).isPresent()) {
                counter++;
                resp += "<tr>\n" +
                        "      <th scope=\"row\">"+counter+"</th>\n" +
                        "      <td>"+card.getName()+"</td>\n" +
                        "      <td>"+card.getDescription()+"</td>\n" +
                        "      <td><button type=\"button\" id=\"editcardbtn"+card.getId()+"\" data-toggle=\"modal\" data-target=\"#editcard" + card.getId() + "\" class=\"btn bg-main text-light\">Посмотреть</button></td>\n" +
                        "    </tr>";
                respmodal+=getCardsUser(card,searchUser.getId());
            }
        }
        if (counter == 0) {
            resp = "Ничего не найдено!";
        } else {
            resp+= "</tbody>\n" +
                    "</table>" + respmodal;
        }
        SearchResponse response = new SearchResponse();
        response.setStatus("success");
        response.setMessageUp("Результатов найдено: " + counter);
        response.setMessageDown(resp);
        return response;
    }
    @ResponseBody
    @PostMapping("/api/searchenginemanager")
    public Object SearchEngineManager(HttpSession session,@RequestParam(name="name", required=false) String name,@RequestParam(name="description", required=false) String description,@RequestParam(name="inputstatus", required=false) String status,@RequestParam(name="inputobject", required=false) Long object,@RequestParam(name="datestart", required=false) String datestart,@RequestParam(name="dateend", required=false) String dateend,@RequestParam(name="fnameauthorcard", required=false) String fnameauthorcard,@RequestParam(name="inameauthorcard", required=false) String inameauthorcard,@RequestParam(name="onameauthorcard", required=false) String onameauthorcard,@RequestParam(name="fnameexcard", required=false) String fnameexcard,@RequestParam(name="inameexcard", required=false) String inameexcard,@RequestParam(name="onameexcard", required=false) String onameexcard) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        class SearchResponse {
            @JsonProperty("status")
            private String status;
            @JsonProperty("messageup")
            private String messageUp;
            @JsonProperty("messagedown")
            private String messageDown;
            public void setStatus(String status) {
                this.status = status;
            }
            public void setMessageUp(String message) {
                this.messageUp = message;
            }
            public void setMessageDown(String message) {
                this.messageDown = message;
            }
        }
        List<Card> cards = CardManager.readAllByNameIgnoreCase(name.describeConstable(),description.describeConstable(),status.describeConstable(),object);
        String resp = "<table class=\"table\">\n" +
                "  <thead>\n" +
                "    <tr>\n" +
                "      <th scope=\"col\">#</th>\n" +
                "      <th scope=\"col\">Название</th>\n" +
                "      <th scope=\"col\">Описание</th>\n" +
                "      <th scope=\"col\">Редактирование</th>\n" +
                "    </tr>\n" +
                "  </thead>\n" +
                "  <tbody>";
        String respmodal = "";
        int counter = 0;
        String mail = (String) session.getAttribute("user");
        User searchUser = userManager.getUserByMail(mail).get();
        for (Card card : cards) {
            User cardUserEx = userManager.getUserById(card.getIdExecutor()).get();
            if (getBetwheenDates(datestart,dateend,card.getTime()) && userManager.searchUser(inameauthorcard,fnameauthorcard,onameauthorcard,searchUser.getId()).isPresent() && userManager.searchUser(inameexcard,fnameexcard,onameexcard,cardUserEx.getId()).isPresent()) {
                counter++;
                resp += "<tr>\n" +
                        "      <th scope=\"row\">"+counter+"</th>\n" +
                        "      <td>"+card.getName()+"</td>\n" +
                        "      <td>"+card.getDescription()+"</td>\n" +
                        "      <td><button type=\"button\" id=\"editcardbtn"+card.getId()+"\" data-toggle=\"modal\" data-target=\"#editcard" + card.getId() + "\" class=\"btn bg-main text-light\">Посмотреть</button></td>\n" +
                        "    </tr>";
                respmodal+=getCardsUser(card,searchUser.getId());
            }
        }
        if (counter == 0) {
            resp = "Ничего не найдено!";
        } else {
            resp+= "</tbody>\n" +
                    "</table>" + respmodal;
        }
        SearchResponse response = new SearchResponse();
        response.setStatus("success");
        response.setMessageUp("Результатов найдено: " + counter);
        response.setMessageDown(resp);
        return response;
    }
    @ResponseBody
    @PostMapping("/api/searchengineadmin")
    public Object SearchEngineAdmin(HttpSession session,@RequestParam(name="mail", required=false) String mail,@RequestParam(name="inputrole", required=false) Long inputRole,@RequestParam(name="datestart", required=false) String datestart,@RequestParam(name="dateend", required=false) String dateend,@RequestParam(name="fname", required=false) String fname,@RequestParam(name="iname", required=false) String iname,@RequestParam(name="oname", required=false) String oname) {
        @JsonInclude(JsonInclude.Include.NON_NULL)
        class SearchResponse {
            @JsonProperty("status")
            private String status;
            @JsonProperty("messageup")
            private String messageUp;
            @JsonProperty("messagedown")
            private String messageDown;
            public void setStatus(String status) {
                this.status = status;
            }
            public void setMessageUp(String message) {
                this.messageUp = message;
            }
            public void setMessageDown(String message) {
                this.messageDown = message;
            }
        }
        List<User> users = UserManager.searchadmin(iname.describeConstable(),fname.describeConstable(),oname.describeConstable(),mail.describeConstable(),inputRole);
        String resp = "<table class=\"table\">\n" +
                "  <thead>\n" +
                "    <tr>\n" +
                "      <th scope=\"col\">#</th>\n" +
                "      <th scope=\"col\">Фамилия</th>\n" +
                "      <th scope=\"col\">Имя</th>\n" +
                "      <th scope=\"col\">Отчество</th>\n" +
                "      <th scope=\"col\">Почта</th>\n" +
                "      <th scope=\"col\">Дата регистрации</th>\n" +
                "      <th scope=\"col\">Подробнее</th>\n" +
                "    </tr>\n" +
                "  </thead>\n" +
                "  <tbody>";
        String respmodal = "";
        int counter = 0;
        for (User user : users) {
            if (getBetwheenDates(datestart,dateend,user.getTime())) {
                counter++;
                resp += "<tr>\n" +
                        "      <th scope=\"row\">"+counter+"</th>\n" +
                        "      <td>"+user.getFname()+"</td>\n" +
                        "      <td>"+user.getIname()+"</td>\n" +
                        "      <td>"+user.getOname()+"</td>\n" +
                        "      <td>"+user.getMail()+"</td>\n" +
                        "      <td>"+getInNormalDate(user.getTime())+"</td>\n" +
                        "      <td><button type=\"button\" id=\"editcardbtn"+user.getId()+"\" data-toggle=\"modal\" data-target=\"#cardmodal" + user.getId() + "\" class=\"btn bg-main text-light\">Посмотреть</button></td>\n" +
                        "    </tr>";
                respmodal+=getCardsUserModel(user);
            }
        }
        if (counter == 0) {
            resp = "Ничего не найдено!";
        } else {
            resp+= "</tbody>\n" +
                    "</table>" + respmodal;
        }
        SearchResponse response = new SearchResponse();
        response.setStatus("success");
        response.setMessageUp("Результатов найдено: " + counter);
        response.setMessageDown(resp);
        return response;
    }
    @ResponseBody
    @PostMapping("/api/onchangestatus")
    public Boolean onChangeStatus(HttpSession session,@RequestParam(name="status", required=false) String status,@RequestParam(name="idcard", required=false) Long idcard) {
        if (!((status.equals("open") || status.equals("inwork") || status.equals("close")))) {return false;}
        if (CardManager.readAllById(idcard).isEmpty()) {return false;}
        if (UserManager.getUserByMail((String) session.getAttribute("user")).get().getUsertype() != 2) {return false;}
        Card cardUpdate = CardManager.readAllById(idcard).get();
        cardUpdate.setStatus(status);
        CardManager.createCard(cardUpdate);
        return true;
    }

    @ResponseBody
    @PostMapping("/api/savedataadmin")
    public String onSaveDataAdmin(HttpSession session,@RequestParam(name="iduser", required=false) Long idUser,@RequestParam(name="inputid", required=false) String inputid,@RequestParam(name="value", required=false) String value) {
        inputid = inputid.replaceAll("\\d", "");
        User userSave = UserManager.getUserById(idUser).get();
        switch (inputid) {
            case "changemail":
                if (UserManager.getUserByMail(value).orElse(null) != null) {return "Такая почта уже есть";}
                if (!isValidEmail(value)) {return "Почта невалидна!";}
                userSave.setMail(value);
                break;
            case "changefname":
                userSave.setFname(value);
                break;
            case "changeiname":
                userSave.setIname(value);
                break;
            case "changeoname":
                userSave.setOname(value);
                break;
            case "inputRoleForm":
                userSave.setUsertype(Integer.parseInt(value));
                break;
            case "inputManagersForm":
                userSave.setIdManager(Long.parseLong(value));
                break;
        }
        userManager.createUser(userSave);
        return "Сохранено";
    }
}

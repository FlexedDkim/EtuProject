package com.example.servingwebcontent.func;

import ch.qos.logback.core.model.Model;
import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.database.Comment;
import com.example.servingwebcontent.database.File;
import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.managers.CardManager;
import com.example.servingwebcontent.managers.CommentManager;
import com.example.servingwebcontent.managers.FileManager;
import com.example.servingwebcontent.managers.UserManager;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.websocket.Session;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainFunction {
    private static final Set<String> imageFormats = Set.of(".png", ".jpeg", ".jpg", ".gif", ".bmp", ".tiff", ".tif");
    private static final Set<String> documentFormats = Set.of(".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".pdf", ".txt", ".rtf", ".odt", ".ods", ".odp", ".csv");
    private static final Set<String> videoFormats = Set.of(".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv", ".3gp", ".webm");
    private static final Set<String> archiveFormats = Set.of(".zip", ".rar", ".7z", ".tar", ".gz", ".bz");
    private static final Set<String> presentationFormats = Set.of(".ppt", ".pptx", ".odp", ".pdf");
    private static final Set<String> spreadsheetFormats = Set.of(".xls", ".xlsx", ".ods", ".csv");
    private static final Set<String> otherFormats = Set.of(".dwg", ".dxf", ".mdb", ".accdb", ".sql", ".psd", ".ai", ".rvt", ".ifc", ".skp");
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String CHARACTERSNUM = "0123456789";
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String generateRandomString(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public static String generateRandomStringNum(int length) {
        StringBuilder randomString = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(CHARACTERSNUM.length());
            char randomChar = CHARACTERSNUM.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String generateUniqueFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String newFileName = timestamp + "_" + generateRandomString(10);
        return newFileName;
    }
    public static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            return fileName.substring(dotIndex + 1);
        }
        return "";
    }
    public static boolean canUploadFile(String fileName) {

        String fileExtension = '.' + fileName.toLowerCase();

        if (imageFormats.contains(fileExtension)) {
            return true;
        } else if (documentFormats.contains(fileExtension)) {
            return true;
        } else if (videoFormats.contains(fileExtension)) {
            return true;
        } else if (archiveFormats.contains(fileExtension)) {
            return true;
        } else if (presentationFormats.contains(fileExtension)) {
            return true;
        } else if (spreadsheetFormats.contains(fileExtension)) {
            return true;
        } else if (otherFormats.contains(fileExtension)) {
            return true;
        } else {
            return false;
        }
    }

    public static Long getCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        long unixTimestamp = currentTimeMillis / 1000;
        return unixTimestamp;
    }

    public static String getCardsUser(Card card,Long idOwn) {
        UserManager userManager = null;
        Optional<User> userOptional = userManager.getUserById(idOwn);
        User userFunc = userOptional.get();
        User userCardEx = userManager.getUserById(card.getIdExecutor()).get();
        User userCard = userManager.getUserById(card.getIdOwn()).get();
        String cardsHtmlBody = "";
        String filesHtml = "<div id=\"filecontainer"+card.getId()+"\" class=\"container\"><div class=\"card-deck\">";
        List<File> files = FileManager.readAllByIdCard(card.getId());
        for (File file : files) {
            String delBtn = "";
            String deletedAttribyte = "";
            String deletedAttribyteName = "";
            if (file.getDeleted() == true) {
                deletedAttribyte = "disabled";
                deletedAttribyteName = "[Удалено]";
            }
            filesHtml += "<div id=\"filecard"+file.getId()+"\" style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                    "            <div class=\"card-body\">\n" +
                    "                <h5 id=\"namecard"+file.getId()+"\" class=\"card-title\">" + file.getRealName() + " " + deletedAttribyteName + " </h5>\n" +
                    "                <p class=\"card-text\">" + convertFileSize(file.getSize()) + "</p>\n" +
                    "                <button type=\"button\" onclick=\"window.open('/api/download/" + file.getId() + "');\" id=\"btnDownloadFile" + file.getId() + "\" class=\"btn bg-main text-light\" "+deletedAttribyte+">Скачать</button>" +
                    "            </div>\n" +
                    "        </div>";
        }
        String commentsHtml = "";
        List<Comment> comments = CommentManager.readAllByIdCard(card.getId());
        for (Comment comment : comments) {
            User userComment = userManager.getUserById(comment.getIdOwn()).get();
            String orientationComments = "second";
            if (comment.getIdOwn() != idOwn) {orientationComments = "second-resp";}
            String pathToFoto = "";
            if (userComment.getAvatar() == 0) {
                pathToFoto = "../img/noavatar.png";
            }
            else {
                File file = FileManager.readAllById(userComment.getAvatar()).get();
                pathToFoto = "../avatars/" + file.getGenName() + "." + file.getType();
            }
            commentsHtml +=
                    "    <div class=\"d-flex justify-content-center py-2\">\n" +
                            "        <div class=\""+orientationComments+" py-2 px-2\">\n" +
                            "            <span class=\"text1\">" + comment.getBody().replace("\n", "<br>") + "</span>\n" +
                            "            <div class=\"d-flex justify-content-between py-1 pt-2\">\n" +
                            "                <div>\n" +
                            "                    <img src=\""+pathToFoto+"\" width=\"20\">\n" +
                            "                    <span class=\"text2\">" + userComment.getFname() + " " + userComment.getIname().charAt(0) + ". " + userComment.getOname().charAt(0) + "." + "</span>\n" +
                            "                </div>\n" +
                            "                <div>\n" +
                            "                    <span class=\"text3\">" + getInNormalDate(comment.getTime()) + "</span>\n" +
                            "                </div>\n" +
                            "            </div>\n" +
                            "        </div>\n" +
                            "    </div>";
        }
        filesHtml += "</div></div>";
        String status = "";
        String selectedOpen = "";
        String selectedInwork = "";
        String selectedClosed = "";
        switch (card.getStatus()) {
            case "open":
                status = "Открыт";
                selectedOpen = "selected";
                break;
            case "inwork":
                status = "У работника";
                selectedInwork = "selected";
                break;
            case "close":
                status = "Закрыт";
                selectedClosed = "selected";
                break;
        }
        String onchangestatus = "";
        if (userFunc.getUsertype() == 2) {
            onchangestatus = "<div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Статус карточки</label>\n" +
                    "      <select class=\"form-control\" id=\"itemStatusSelect" + card.getId() +"\" onchange=\"onchangestatus('" + card.getId() + "');\" name=\"item\">\n" +
                    "           <option "+selectedOpen+"value=\"open\">Открыт</option>\n" +
                    "           <option "+selectedInwork+"value=\"inwork\">У работника</option>\n" +
                    "           <option "+selectedClosed+" value=\"close\">Закрыт</option>\n" +
                    "      </select>\n" +
                    "       </div>  ";
        }
        else
        {
            onchangestatus = "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Статус</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + status + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  ";
        }
        cardsHtmlBody += "<div class=\"modal fade\" id=\"editcard" + card.getId() + "\" tabindex=\"-1\" aria-labelledby=\"ModalLabel\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h4 class=\"modal-title fs-5\">" + card.getName() + "</h4>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "       <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Описание</label>\n" +
                "          <input type=\"text\" class=\"form-control\" id=\"desccard\" value=\"" + card.getDescription() + "\" placeholder=\"\" disabled>\n" +
                "       </div>  " +
                "       <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Время создания карточки</label>\n" +
                "          <input type=\"text\" class=\"form-control\" value=\"" + getInNormalDate(card.getTime()) + "\" placeholder=\"Введите название вашей карточки\" disabled>\n" +
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Автор карточки</label>\n" +
                "          <input type=\"text\" class=\"form-control\" value=\"" + userCard.getFname() + " " + userCard.getIname() + " " + userCard.getOname() + "\" placeholder=\"\" disabled>\n" +
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Исполнитель</label>\n" +
                "          <input type=\"text\" class=\"form-control\" value=\"" + userCardEx.getFname() + " " + userCardEx.getIname() + " " + userCardEx.getOname() + "\" placeholder=\"\" disabled>\n" +
                "       </div>  "
                + onchangestatus +
                "       <label for=\"FormControl\">Прикреплённые файлы</label>\n"
                + filesHtml +
                "<div class=\"form-group\">\n" +
                "           <label for=\"fileUpload"+ card.getId() +"\" class=\"dropzone\" ondragover=\"onDragOver(event)\" data-my-value=\""+ card.getId() +"\" ondrop=\"onDrop(event)\">\n" +
                "               <input id=\"fileUpload"+ card.getId() +"\" type=\"file\" data-my-value=\""+ card.getId() +"\" name=\"files\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                "           </label>\n" +
                "      </div>" +
                "       <label for=\"FormControl\">Комментарии: <span id=\"commentscounter"+ card.getId() +"\">" + CommentManager.countByIdCard(card.getId()) + "</span></label>\n" +
                "           <div class=\"form-group\">\n" +
                "<div class=\"container justify-content-center mt-3\">\n" +
                " <span id=\"commentsinarea" + card.getId() + "\"> "
                + commentsHtml +
                "</span>" +
                "</div>" +
                "<br>" +
                "               <label for=\"comment\">Ваш комментарий:</label>\n" +
                "               <textarea class=\"form-control\" id=\"newcomment" + card.getId() + "\" rows=\"3\"></textarea>\n" +
                "           </div>\n" +
                "           <button type=\"button\" class=\"btn bg-main text-light\" onclick=\"submitComment('" + card.getId() + "','newcomment"+ card.getId() +"','commentsinarea" + card.getId() + "')\">Добавить комментарий</button>" +
                "      </div>\n" +
                "      <div class=\"modal-footer\">\n" +
                "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div></div></div>";
        return cardsHtmlBody;
    }

    public static LocalDateTime getInNormalDate(Long Unix) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Unix), ZoneId.of("Europe/Moscow"));
    }

    public static String convertFileSize(long fileSizeInBytes) {
        if (fileSizeInBytes < 1024) {
            return fileSizeInBytes + " B";
        } else if (fileSizeInBytes < 1024 * 1024) {
            double sizeInKB = fileSizeInBytes / 1024.0;
            return String.format("%.2f KB", sizeInKB);
        } else if (fileSizeInBytes < 1024 * 1024 * 1024) {
            double sizeInMB = fileSizeInBytes / (1024.0 * 1024);
            return String.format("%.2f MB", sizeInMB);
        } else {
            double sizeInGB = fileSizeInBytes / (1024.0 * 1024 * 1024);
            return String.format("%.2f GB", sizeInGB);
        }
    }

    public static String getSettingsUser(Long idUser) {
        UserManager userManager = null;
        Optional<User> userOptional = userManager.getUserById(idUser);
        User userFunc = userOptional.get();
        String prefix = "";
        switch (userFunc.getUsertype()) {
            case 1:
                prefix = "Рабочий";
                break;
            case 2:
                prefix = "Менеджер";
                break;
            case 3:
                prefix = "Администратор";
                break;
        }
        String pathToFoto = "";
        if (userFunc.getAvatar() == 0) {
            pathToFoto = "../img/noavatar.png";
        }
        else {
            File file = FileManager.readAllById(userFunc.getAvatar()).get();
            pathToFoto = "../avatars/" + file.getGenName() + "." + file.getType();
        }
        String mainHtml = "<section style=\"background-color: #FF8C00;\">\n" +
                "  <div class=\"container py-5\">\n" +
                "    <div class=\"row\">\n" +
                "      <div class=\"col-lg-4\">\n" +
                "        <div class=\"card mb-4\">\n" +
                "          <div class=\"card-body text-center\">\n" +
                "<input type=\"file\" id=\"fileInput\" style=\"display: none;\">" +
                "<div class=\"image-upload-wrapper\">\n" +
                "    <img src=\""+pathToFoto+"\" alt=\"Загружаемое изображение\" class=\"rounded-circle img-fluid upload-image\"/>\n" +
                "    <div class=\"overlay\">\n" +
                "        <div class=\"upload-icon\" style=\"display: none;\">Загрузить (только файлы формата png, jpg, jpeg)</div>\n" +
                "    </div>\n" +
                "</div>" +
                "            <h5 class=\"my-3\">"+ userFunc.getFname() + " " + userFunc.getIname() +"</h5>\n" +
                "            <p class=\"text-muted mb-1\">" + prefix + "</p>           \n" +
                "          </div>\n" +
                "        </div>\n" +
                "      </div>\n" +
                "      <div class=\"col-lg-8\">\n" +
                "        <div class=\"card mb-4\">\n" +
                "          <div class=\"card-body\">\n" +
                "            <div class=\"row\">\n" +
                "              <div class=\"col-sm-3\">\n" +
                "                <p class=\"mb-0\">ФИО</p>\n" +
                "              </div>\n" +
                "              <div class=\"col-sm-9\">\n" +
                "                <p class=\"text-muted mb-0\">" + userFunc.getFname() + " " + userFunc.getIname() + " " + userFunc.getOname() + "</p>\n" +
                "              </div>\n" +
                "            </div>\n" +
                "            <hr>\n" +
                "            <div class=\"row justify-content-center align-items-center\">\n" +
                "              <div class=\"col-sm-3\">\n" +
                "                <p class=\"mb-0\">Почта</p>\n" +
                "              </div>\n" +
                "              <div class=\"col-sm-9\">\n" +
                "                <p class=\"text-muted mb-0\">" + userFunc.getMail() + " <button type=\"submit\" data-toggle=\"modal\" data-target=\"#modalRestoreMail\" class=\"btn bg-main text-light\">Изменить почту</button></p>\n" +
                "              </div>\n" +
                "            </div>             \n" +
                "            <hr>\n" +
                "            <div class=\"row justify-content-center align-items-center\">\n" +
                "              <div class=\"col-sm-3\">\n" +
                "                <p class=\"mb-0\">Пароль</p>\n" +
                "              </div>\n" +
                "              <div class=\"col-sm-9\">\n" +
                "                <p class=\"text-muted mb-0\"><button type=\"submit\" data-toggle=\"modal\" data-target=\"#modalRestorePass\" class=\"btn bg-main text-light\">Изменить пароль</button></p>\n" +
                "              </div>\n" +
                "            </div>             \n" +
                "          </div>\n" +
                "        </div>       \n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</section>" +
                "<div class=\"modal fade\" id=\"modalRestoreMail\" tabindex=\"-1\" role=\"dialog\" data-backdrop=\"static\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\" role=\"document\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h5 class=\"modal-title\">Сброс почты</h5>\n" +
                "        <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                "          <span aria-hidden=\"true\">&times;</span>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "         <div class=\"form-group\">\n" +
                "            <label for=\"FormControl\" id=\"newmailresp\">Введите почту</label>\n" +
                "            <input type=\"text\" class=\"form-control\" id=\"newmail\" placeholder=\"Введите новую электронную почту\">\n" +
                "        </div>" +
                "        <button type=\"submit\" id=\"newmailbtn\" class=\"btn bg-main text-light\">Изменить почту</button>" +
                "      </div>\n" +
                "      <div class=\"modal-footer\">\n" +
                "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>" +
                "<div class=\"modal fade\" id=\"modalRestoreMailCode\" tabindex=\"-1\" role=\"dialog\" data-backdrop=\"static\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\" role=\"document\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h5 class=\"modal-title\">Сброс почты</h5>\n" +
                "        <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                "          <span aria-hidden=\"true\">&times;</span>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "         <div class=\"form-group\">\n" +
                "            <label for=\"FormControl\" id=\"newcoderesp\">Введите код</label>\n" +
                "            <input type=\"text\" class=\"form-control\" id=\"newcode\" placeholder=\"Введите код, который пришёл вам на старую почту\">\n" +
                "        </div>" +
                "        <button type=\"submit\" id=\"newcodebtn\" class=\"btn bg-main text-light\">Ввести код</button>" +
                "      </div>\n" +
                "      <div class=\"modal-footer\">\n" +
                "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>" +
                "<div class=\"modal fade\" id=\"modalRestorePass\" tabindex=\"-1\" role=\"dialog\" data-backdrop=\"static\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\" role=\"document\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h5 class=\"modal-title\">Сброс пароля</h5>\n" +
                "        <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                "          <span aria-hidden=\"true\">&times;</span>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "         <div class=\"form-group\">\n" +
                "            <label for=\"FormControl\" id=\"newpassresp\">Введите пароль</label>\n" +
                "            <input type=\"password\" class=\"form-control\" id=\"newpass\" placeholder=\"Введите новый пароль\">\n" +
                "        </div>" +
                "         <div class=\"form-group\">\n" +
                "            <label for=\"FormControl\">Повторите пароль</label>\n" +
                "            <input type=\"password\" class=\"form-control\" id=\"newpassrepeat\" placeholder=\"Повторите новый пароль\">\n" +
                "        </div>" +
                "        <button type=\"submit\" id=\"newpassbtn\" class=\"btn bg-main text-light\">Изменить пароль</button>" +
                "      </div>\n" +
                "      <div class=\"modal-footer\">\n" +
                "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>" +
                "<div class=\"modal fade\" id=\"modalRestorePassCode\" tabindex=\"-1\" role=\"dialog\" data-backdrop=\"static\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\" role=\"document\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h5 class=\"modal-title\">Сброс пароля</h5>\n" +
                "        <button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-label=\"Close\">\n" +
                "          <span aria-hidden=\"true\">&times;</span>\n" +
                "        </button>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "         <div class=\"form-group\">\n" +
                "            <label for=\"FormControl\" id=\"newcodepassresp\">Введите код</label>\n" +
                "            <input type=\"text\" class=\"form-control\" id=\"newcodepass\" placeholder=\"Введите код, который пришёл вам привязанную почту\">\n" +
                "        </div>" +
                "        <button type=\"submit\" id=\"newcodepassbtn\" class=\"btn bg-main text-light\">Ввести код</button>" +
                "      </div>\n" +
                "      <div class=\"modal-footer\">\n" +
                "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";

        return mainHtml;
    }

    public static String getCommentsUser(Long idOwn) {
        UserManager userManager = null;
        Optional<User> userOptional = userManager.getUserById(idOwn);
        User userFunc = userOptional.get();
        List<Card> cards = CardManager.readAllByIdExecutorAndStatus(userFunc.getId(),"inwork");

        String cardsHtmlBody = "";
        String cardsHtml = "<div class=\"container\"><div class=\"card-deck\">";
        for (Card card : cards) {
            User userCardEx = userManager.getUserById(card.getIdExecutor()).get();
            User userCard = userManager.getUserById(card.getIdOwn()).get();
            String filesHtml = "<div id=\"filecontainer"+card.getId()+"\" class=\"container\"><div class=\"card-deck\">";
            List<File> files = FileManager.readAllByIdCard(card.getId());
            for (File file : files) {
                String delBtn = "";
                String deletedAttribyte = "";
                String deletedAttribyteName = "";
                if (file.getDeleted() == true) {
                    deletedAttribyte = "disabled";
                    deletedAttribyteName = "[Удалено]";
                }
                filesHtml += "<div id=\"filecard"+file.getId()+"\" style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                        "            <div class=\"card-body\">\n" +
                        "                <h5 id=\"namecard"+file.getId()+"\" class=\"card-title\">" + file.getRealName() + " " + deletedAttribyteName + " </h5>\n" +
                        "                <p class=\"card-text\">" + convertFileSize(file.getSize()) + "</p>\n" +
                        "                <button type=\"button\" onclick=\"window.open('/api/download/" + file.getId() + "');\" id=\"btnDownloadFile" + file.getId() + "\" class=\"btn bg-main text-light\" "+deletedAttribyte+">Скачать</button>" +
                        "            </div>\n" +
                        "        </div>";
            }
            String commentsHtml = "";
            List<Comment> comments = CommentManager.readAllByIdCard(card.getId());
            for (Comment comment : comments) {
                User userComment = userManager.getUserById(comment.getIdOwn()).get();
                String orientationComments = "second";
                if (comment.getIdOwn() != idOwn) {orientationComments = "second-resp";}
                String pathToFoto = "";
                if (userComment.getAvatar() == 0) {
                    pathToFoto = "../img/noavatar.png";
                }
                else {
                    File file = FileManager.readAllById(userComment.getAvatar()).get();
                    pathToFoto = "../avatars/" + file.getGenName() + "." + file.getType();
                }
                commentsHtml +=
                        "    <div class=\"d-flex justify-content-center py-2\">\n" +
                                "        <div class=\""+orientationComments+" py-2 px-2\">\n" +
                                "            <span class=\"text1\">" + comment.getBody().replace("\n", "<br>") + "</span>\n" +
                                "            <div class=\"d-flex justify-content-between py-1 pt-2\">\n" +
                                "                <div>\n" +
                                "                    <img src=\""+pathToFoto+"\" width=\"20\">\n" +
                                "                    <span class=\"text2\">" + userComment.getFname() + " " + userComment.getIname().charAt(0) + ". " + userComment.getOname().charAt(0) + "." + "</span>\n" +
                                "                </div>\n" +
                                "                <div>\n" +
                                "                    <span class=\"text3\">" + getInNormalDate(comment.getTime()) + "</span>\n" +
                                "                </div>\n" +
                                "            </div>\n" +
                                "        </div>\n" +
                                "    </div>";
            }
            filesHtml += "</div></div>";
            String status = "";
            switch (card.getStatus()) {
                case "open":
                    status = "Открыт";
                    break;
                case "inwork":
                    status = "У работника";
                    break;
                case "close":
                    status = "Закрыт";
                    break;
            }
            cardsHtmlBody += "<div style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                    "            <div class=\"card-body\">\n" +
                    "                <h5 class=\"card-title\">" + card.getName() + "</h5>\n" +
                    "                <p class=\"card-text\">" + card.getDescription() + "</p>\n" +
                    "                <button type=\"button\" id=\"cardButton" + card.getId() + "\" class=\"btn bg-main text-light\" data-toggle=\"modal\" data-target=\"#cardmodal" + card.getId() + "\">Подробнее</button>" +
                    "            </div>\n" +
                    "        </div>";
            cardsHtmlBody += "<div class=\"modal fade\" id=\"cardmodal" + card.getId() + "\" tabindex=\"-1\" aria-labelledby=\"ModalLabel\" aria-hidden=\"true\">\n" +
                    "  <div class=\"modal-dialog modal-dialog-centered\">\n" +
                    "    <div class=\"modal-content\">\n" +
                    "      <div class=\"modal-header\">\n" +
                    "        <h4 class=\"modal-title fs-5\">" + card.getName() + "</h4>\n" +
                    "      </div>\n" +
                    "      <div class=\"modal-body\">\n" +
                    "       <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Описание</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" id=\"desccard\" value=\"" + card.getDescription() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "       <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Время создания карточки</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + getInNormalDate(card.getTime()) + "\" placeholder=\"Введите название вашей карточки\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Автор карточки</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + userCard.getFname() + " " + userCard.getIname() + " " + userCard.getOname() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Исполнитель</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + userCardEx.getFname() + " " + userCardEx.getIname() + " " + userCardEx.getOname() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Статус</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + status + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "       <label for=\"FormControl\">Прикреплённые файлы</label>\n"
                    + filesHtml +
                    "<div class=\"form-group\">\n" +
                    "           <label for=\"fileUpload"+ card.getId() +"\" class=\"dropzone\" ondragover=\"onDragOver(event)\" data-my-value=\""+ card.getId() +"\" ondrop=\"onDrop(event)\">\n" +
                    "               <input id=\"fileUpload"+ card.getId() +"\" type=\"file\" data-my-value=\""+ card.getId() +"\" name=\"files\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                    "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                    "           </label>\n" +
                    "      </div>" +
                    "       <label for=\"FormControl\">Комментарии: <span id=\"commentscounter"+ card.getId() +"\">" + CommentManager.countByIdCard(card.getId()) + "</span></label>\n" +
                    "           <div class=\"form-group\">\n" +
                    "<div class=\"container justify-content-center mt-3\">\n" +
                    " <span id=\"commentsinarea" + card.getId() + "\"> "
                    + commentsHtml +
                    "</span>" +
                    "</div>" +
                    "<br>" +
                    "               <label for=\"comment\">Ваш комментарий:</label>\n" +
                    "               <textarea class=\"form-control\" id=\"newcomment" + card.getId() + "\" rows=\"3\"></textarea>\n" +
                    "           </div>\n" +
                    "           <button type=\"button\" class=\"btn bg-main text-light\" onclick=\"submitComment('" + card.getId() + "','newcomment"+ card.getId() +"','commentsinarea" + card.getId() + "')\">Добавить комментарий</button>" +
                    "      </div>\n" +
                    "      <div class=\"modal-footer\">\n" +
                    "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>";
        }
        if (cardsHtmlBody != "") {
            cardsHtml += cardsHtmlBody + "</div></div>";
        }
        else
        {
            cardsHtml = "<h1 class=\"display-4\">У вас на доработке нет ни одной карточки</h1>\n" +
                    "<p>Карточки появятся, как только кто-то отправит вам их на доработку!</p>";
        }
        return cardsHtml;
    }

    public static String getCommentsForManager(Long idOwn) {
        UserManager userManager = null;
        List<Card> cards = CardManager.readAllByStatus("open");

        String cardsHtmlBody = "";
        String cardsHtml = "<div class=\"container\"><div class=\"card-deck\">";
        for (Card card : cards) {
            User userCard = userManager.getUserById(card.getIdOwn()).get();
            User userCardEx = userManager.getUserById(card.getIdExecutor()).get();
            if (idOwn != userCardEx.getIdManager()) {continue;}
            String filesHtml = "<div id=\"filecontainer"+card.getId()+"\" class=\"container\"><div class=\"card-deck\">";
            List<File> files = FileManager.readAllByIdCard(card.getId());
            for (File file : files) {
                String delBtn = "";
                String deletedAttribyte = "";
                String deletedAttribyteName = "";
                if (file.getDeleted() == true) {
                    deletedAttribyte = "disabled";
                    deletedAttribyteName = "[Удалено]";
                }
                filesHtml += "<div id=\"filecard"+file.getId()+"\" style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                        "            <div class=\"card-body\">\n" +
                        "                <h5 id=\"namecard"+file.getId()+"\" class=\"card-title\">" + file.getRealName() + " " + deletedAttribyteName + " </h5>\n" +
                        "                <p class=\"card-text\">" + convertFileSize(file.getSize()) + "</p>\n" +
                        "                <button type=\"button\" onclick=\"window.open('/api/download/" + file.getId() + "');\" id=\"btnDownloadFile" + file.getId() + "\" class=\"btn bg-main text-light\" "+deletedAttribyte+">Скачать</button>" +
                        "            </div>\n" +
                        "        </div>";
            }
            String commentsHtml = "";
            List<Comment> comments = CommentManager.readAllByIdCard(card.getId());
            for (Comment comment : comments) {
                User userComment = userManager.getUserById(comment.getIdOwn()).get();
                String orientationComments = "second";
                if (comment.getIdOwn() != idOwn) {orientationComments = "second-resp";}
                String pathToFoto = "";
                if (userComment.getAvatar() == 0) {
                    pathToFoto = "../img/noavatar.png";
                }
                else {
                    File file = FileManager.readAllById(userComment.getAvatar()).get();
                    pathToFoto = "../avatars/" + file.getGenName() + "." + file.getType();
                }
                commentsHtml +=
                        "    <div class=\"d-flex justify-content-center py-2\">\n" +
                                "        <div class=\""+orientationComments+" py-2 px-2\">\n" +
                                "            <span class=\"text1\">" + comment.getBody().replace("\n", "<br>") + "</span>\n" +
                                "            <div class=\"d-flex justify-content-between py-1 pt-2\">\n" +
                                "                <div>\n" +
                                "                    <img src=\""+pathToFoto+"\" width=\"20\">\n" +
                                "                    <span class=\"text2\">" + userComment.getFname() + " " + userComment.getIname().charAt(0) + ". " + userComment.getOname().charAt(0) + "." + "</span>\n" +
                                "                </div>\n" +
                                "                <div>\n" +
                                "                    <span class=\"text3\">" + getInNormalDate(comment.getTime()) + "</span>\n" +
                                "                </div>\n" +
                                "            </div>\n" +
                                "        </div>\n" +
                                "    </div>";
            }
            filesHtml += "</div></div>";
            cardsHtmlBody += "<div style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                    "            <div class=\"card-body\">\n" +
                    "                <h5 class=\"card-title\">" + card.getName() + "</h5>\n" +
                    "                <p class=\"card-text\">" + card.getDescription() + "</p>\n" +
                    "                <button type=\"button\" id=\"cardButton" + card.getId() + "\" class=\"btn bg-main text-light\" data-toggle=\"modal\" data-target=\"#cardmodal" + card.getId() + "\">Подробнее</button>" +
                    "            </div>\n" +
                    "        </div>";
            cardsHtmlBody += "<div class=\"modal fade\" id=\"cardmodal" + card.getId() + "\" tabindex=\"-1\" aria-labelledby=\"ModalLabel\" aria-hidden=\"true\">\n" +
                    "  <div class=\"modal-dialog modal-dialog-centered\">\n" +
                    "    <div class=\"modal-content\">\n" +
                    "      <div class=\"modal-header\">\n" +
                    "        <h4 class=\"modal-title fs-5\">" + card.getName() + "</h4>\n" +
                    "      </div>\n" +
                    "      <div class=\"modal-body\">\n" +
                    "       <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Описание</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" id=\"desccard\" value=\"" + card.getDescription() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "       <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Время создания карточки</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + getInNormalDate(card.getTime()) + "\" placeholder=\"Введите название вашей карточки\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Автор карточки</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + userCard.getFname() + " " + userCard.getIname() + " " + userCard.getOname() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Исполнитель</label>\n" +
                    "          <input type=\"text\" class=\"form-control\" value=\"" + userCardEx.getFname() + " " + userCardEx.getIname() + " " + userCardEx.getOname() + "\" placeholder=\"\" disabled>\n" +
                    "       </div>  " +
                    "        <div class=\"form-group\">\n" +
                    "          <label for=\"FormControl\">Статус карточки</label>\n" +
                    "      <select class=\"form-control\" id=\"itemStatusSelect" + card.getId() +"\" onchange=\"onchangestatus('" + card.getId() + "');\" name=\"item\">\n" +
                    "           <option value=\"open\">Открыт</option>\n" +
                    "           <option value=\"inwork\">У работника</option>\n" +
                    "           <option value=\"close\">Закрыт</option>\n" +
                    "      </select>\n" +
                    "       </div>  " +
                    "       <label for=\"FormControl\">Прикреплённые файлы</label>\n"
                    + filesHtml +
                    "<div class=\"form-group\">\n" +
                    "           <label for=\"fileUpload"+ card.getId() +"\" class=\"dropzone\" ondragover=\"onDragOver(event)\" data-my-value=\""+ card.getId() +"\" ondrop=\"onDrop(event)\">\n" +
                    "               <input id=\"fileUpload"+ card.getId() +"\" type=\"file\" data-my-value=\""+ card.getId() +"\" name=\"files\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                    "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                    "           </label>\n" +
                    "      </div>" +
                    "       <label for=\"FormControl\">Комментарии: <span id=\"commentscounter"+ card.getId() +"\">" + CommentManager.countByIdCard(card.getId()) + "</span></label>\n" +
                    "           <div class=\"form-group\">\n" +
                    "<div class=\"container justify-content-center mt-3\">\n" +
                    " <span id=\"commentsinarea" + card.getId() + "\"> "
                    + commentsHtml +
                    "</span>" +
                    "</div>" +
                    "<br>" +
                    "               <label for=\"comment\">Ваш комментарий:</label>\n" +
                    "               <textarea class=\"form-control\" id=\"newcomment" + card.getId() + "\" rows=\"3\"></textarea>\n" +
                    "           </div>\n" +
                    "           <button type=\"button\" class=\"btn bg-main text-light\" onclick=\"submitComment('" + card.getId() + "','newcomment"+ card.getId() +"','commentsinarea" + card.getId() + "')\">Добавить комментарий</button>" +
                    "      </div>\n" +
                    "      <div class=\"modal-footer\">\n" +
                    "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Закрыть</button>\n" +
                    "      </div>\n" +
                    "    </div>\n" +
                    "  </div>\n" +
                    "</div>";
        }
        if (cardsHtmlBody != "") {
            cardsHtml += cardsHtmlBody + "</div></div>";
        }
        else
        {
            cardsHtml = "<h1 class=\"display-4\">У вас на проверке нет ни одной карточки</h1>\n" +
                    "<p>Карточки появятся, как только кто-то отправит вам их на доработку!</p>";
        }
        return cardsHtml;
    }

    public static String getSearchBarUserEdit(Long idOwn) {
        String searchBar = "<section class=\"search-banner py-5\" id=\"search-banner\">\n" +
                "    <div class=\"container py-5 my-5\">\n" +
                "    <div class=\"row text-dark text-center pb-4\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <h2 id=\"respsearch\">Поиск</h2>\n" +
                "        </div>\n" +
                "    </div>   \n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <div class=\"card\">\n" +
                "                <div class=\"card-body\">\n" +
                "                    <div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"datestart\" placeholder=\"Поиск с\" aria-label=\"Start date\">" +
                " <small class=\"form-text text-muted\">Дата, с которой будет производиться поиск</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"dateend\" placeholder=\"Поиск до\" aria-label=\"End date\">" +
                " <small class=\"form-text text-muted\">Дата, до которой будет производиться поиск (Не включительно)</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                "                          <select id=\"inputStatus\" class=\"form-control\" >\n" +
                "                            <option value=\"open\" selected>Открыт</option>\n" +
                "                            <option value=\"inwork\">На проверке</option>\n" +
                "                            <option value=\"close\">Закрыт</option>\n" +
                "                            \n" +
                "                          </select>\n" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                "                          <select id=\"inputObject\" class=\"form-control\" >\n" +
                "           <option value=\"1\" selected>“Восьмое чудо света” – город Великий Новгород, улица Новолучанская, дом 3</option>\n" +
                "           <option value=\"2\">“Наша эпоха” – город Нижневартовск, улица Нефтяников, дом 91</option>\n" +
                "           <option value=\"3\">“Изобилие цветов” – город Санкт-Петербург, Невский проспект, дом 49</option>\n" +
                "           <option value=\"4\">“Великодушие Бога” – город Великие Луки, улица Ухтомского, дом 72</option>\n" +
                "           <option value=\"5\">“Дьявольский соблазн” – город Санкт-Петербург, улица Московская, дом 115</option>\n" +
                "                          </select>\n" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"fnameauthorcard\" type=\"text\" placeholder=\"Фамилия автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"inameauthorcard\" type=\"text\" placeholder=\"Имя автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"onameauthorcard\" type=\"text\" placeholder=\"Отчество автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"namecard\" type=\"text\" placeholder=\"Название карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"descriptioncard\" type=\"text\" placeholder=\"Описание карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <button type=\"button\" id=\"searchstart\" class=\"btn bg-main text-light\">Найти!</button>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "        </div>\n" +
                "    </div>\n" +
                "<div id=\"respsearchbottom\"></div>" +
                "</div>\n" +
                "</section>";
        return searchBar;
    }

    public static Long getDateLongInDate(String dateString) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(dateString, formatter);
        return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;

    }

    public static Boolean getBetwheenDates(String datestart, String dateend, Long datecurrent) {
        if (datestart.trim() == "" && dateend.trim() == "") {
            return true;
        }
        if (datestart.trim() != "" && dateend.trim() == "") {
            if (getDateLongInDate(datestart) < datecurrent) {return true;} else {return false;}
        }
        if (datestart.trim() == "" && dateend.trim() != "") {
            if (getDateLongInDate(dateend) > datecurrent) {return true;} else {return false;}
        }
        if (datestart.trim() != "" && dateend.trim() != "") {
            if (getDateLongInDate(dateend) > datecurrent && getDateLongInDate(datestart) < datecurrent) {return true;} else {
            }
        }
        return false;
    }

    public static String getSearchBarManagerEdit(Long idUser) {
        String searchBar = "<section class=\"search-banner py-5\" id=\"search-banner\">\n" +
                "    <div class=\"container py-5 my-5\">\n" +
                "    <div class=\"row text-dark text-center pb-4\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <h2 id=\"respsearch\">Поиск</h2>\n" +
                "        </div>\n" +
                "    </div>   \n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <div class=\"card\">\n" +
                "                <div class=\"card-body\">\n" +
                "                    <div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"datestart\" placeholder=\"Поиск с\" aria-label=\"Start date\">" +
                " <small class=\"form-text text-muted\">Дата, с которой будет производиться поиск</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"dateend\" placeholder=\"Поиск до\" aria-label=\"End date\">" +
                " <small class=\"form-text text-muted\">Дата, до которой будет производиться поиск (Не включительно)</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                "                          <select id=\"inputStatus\" class=\"form-control\" >\n" +
                "                            <option value=\"open\" selected>Открыт</option>\n" +
                "                            <option value=\"inwork\">На доработке</option>\n" +
                "                            <option value=\"close\">Закрыт</option>\n" +
                "                            \n" +
                "                          </select>\n" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                "                          <select id=\"inputObject\" class=\"form-control\" >\n" +
                "           <option value=\"1\" selected>“Восьмое чудо света” – город Великий Новгород, улица Новолучанская, дом 3</option>\n" +
                "           <option value=\"2\">“Наша эпоха” – город Нижневартовск, улица Нефтяников, дом 91</option>\n" +
                "           <option value=\"3\">“Изобилие цветов” – город Санкт-Петербург, Невский проспект, дом 49</option>\n" +
                "           <option value=\"4\">“Великодушие Бога” – город Великие Луки, улица Ухтомского, дом 72</option>\n" +
                "           <option value=\"5\">“Дьявольский соблазн” – город Санкт-Петербург, улица Московская, дом 115</option>\n" +
                "                          </select>\n" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"namecard\" type=\"text\" placeholder=\"Название карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"descriptioncard\" type=\"text\" placeholder=\"Описание карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"fnameexcard\" type=\"text\" placeholder=\"Фамилия исполнителя карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"inameexcard\" type=\"text\" placeholder=\"Имя исполнителя карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"onameexcard\" type=\"text\" placeholder=\"Отчество исполнителя карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"fnameauthorcard\" type=\"text\" placeholder=\"Фамилия автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"inameauthorcard\" type=\"text\" placeholder=\"Имя автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"onameauthorcard\" type=\"text\" placeholder=\"Отчество автора карточки\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <button type=\"button\" id=\"searchstartformanager\" class=\"btn bg-main text-light\">Найти!</button>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "        </div>\n" +
                "    </div>\n" +
                "<div id=\"respsearchbottom\"></div>" +
                "</div>\n" +
                "</section>";
        return searchBar;
    }
    public static String getCreateCardManager(Long idUser) {
        String selected = "";
        List<User> users = UserManager.getUserByIdManager(idUser);

        for (User user : users) {
            selected+="<option value=\""+user.getId()+"\">"+ user.getFname()+" " +user.getIname()+ " " +user.getOname()+ " ("+user.getMail()+")</option>";
        }
        return "<h1 class=\"display-4\">Создание карточки</h1>  " +
                "<div id=\"progressBar\"></div>" +
                "<div class=\"form-group\">\n" +
                "    <label for=\"FormControl\">Название</label>\n" +
                "    <input type=\"text\" class=\"form-control\" id=\"namecard\" placeholder=\"Введите название вашей карточки\">\n" +
                "  </div>  " +
                "<div class=\"form-group\">\n" +
                "    <label for=\"FormControl\">Описание</label>\n" +
                "    <input type=\"text\" class=\"form-control\" id=\"desccard\" placeholder=\"Введите описание вашей карточки\">\n" +
                "  </div>" +
                "      <div class=\"form-group\">\n" +
                "      <label for=\"itemSelect\">Выберите пункт:</label>\n" +
                "      <select class=\"form-control\" id=\"itemSelect\" name=\"item\">\n" +
                "           <option value=\"1\">“Восьмое чудо света” – город Великий Новгород, улица Новолучанская, дом 3</option>\n" +
                "           <option value=\"2\">“Наша эпоха” – город Нижневартовск, улица Нефтяников, дом 91</option>\n" +
                "           <option value=\"3\">“Изобилие цветов” – город Санкт-Петербург, Невский проспект, дом 49</option>\n" +
                "           <option value=\"4\">“Великодушие Бога” – город Великие Луки, улица Ухтомского, дом 72</option>\n" +
                "<option value=\"5\">“Дьявольский соблазн” – город Санкт-Петербург, улица Московская, дом 115</option>\n" +
                "      </select>\n" +
                "      </div>" +
                "      <div class=\"form-group\">\n" +
                "      <label for=\"itemSelect\">Выберите исполнителя:</label>\n" +
                "      <select class=\"form-control\" id=\"itemSelectEmployer\" name=\"item\">\n" +
                selected +
                "      </select>\n" +
                "      </div>" +
                "      <div class=\"form-group\">\n" +
                "           <label for=\"fileUpload1\" class=\"dropzone\" data-my-value=\"1\" ondragover=\"onDragOver(event)\" ondrop=\"onDrop(event)\">\n" +
                "               <input id=\"fileUpload1\" type=\"file\" name=\"files\" data-my-value=\"1\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                "           </label>\n" +
                "      </div>\n" +
                "      <button type=\"submit\" id=\"uploadButton\" class=\"btn bg-main text-light\">Сохранить</button>\n";
    }

    public static String getCreateCardUser() {
        return "<h1 class=\"display-4\">Загрузка файлов</h1>  " +
                "<div id=\"progressBar\"></div>" +
                "<div class=\"form-group\">\n" +
                "    <label for=\"FormControl\">Название</label>\n" +
                "    <input type=\"text\" class=\"form-control\" id=\"namecard\" placeholder=\"Введите название вашей карточки\">\n" +
                "  </div>  " +
                "<div class=\"form-group\">\n" +
                "    <label for=\"FormControl\">Описание</label>\n" +
                "    <input type=\"text\" class=\"form-control\" id=\"desccard\" placeholder=\"Введите описание вашей карточки\">\n" +
                "  </div>" +
                "      <div class=\"form-group\">\n" +
                "      <label for=\"itemSelect\">Выберите пункт:</label>\n" +
                "      <select class=\"form-control\" id=\"itemSelect\" name=\"item\">\n" +
                "           <option value=\"1\">“Восьмое чудо света” – город Великий Новгород, улица Новолучанская, дом 3</option>\n" +
                "           <option value=\"2\">“Наша эпоха” – город Нижневартовск, улица Нефтяников, дом 91</option>\n" +
                "           <option value=\"3\">“Изобилие цветов” – город Санкт-Петербург, Невский проспект, дом 49</option>\n" +
                "           <option value=\"4\">“Великодушие Бога” – город Великие Луки, улица Ухтомского, дом 72</option>\n" +
                "<option value=\"5\">“Дьявольский соблазн” – город Санкт-Петербург, улица Московская, дом 115</option>\n" +
                "      </select>\n" +
                "      </div>" +
                "      <div class=\"form-group\">\n" +
                "           <label for=\"fileUpload1\" class=\"dropzone\" data-my-value=\"1\" ondragover=\"onDragOver(event)\" ondrop=\"onDrop(event)\">\n" +
                "               <input id=\"fileUpload1\" type=\"file\" name=\"files\" data-my-value=\"1\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                "           </label>\n" +
                "      </div>\n" +
                "      <button type=\"submit\" id=\"uploadButton\" class=\"btn bg-main text-light\">Сохранить</button>\n";
    }

    public static String searchAndEdit(Long idUser) {
        String searchBar = "<section class=\"search-banner py-5\" id=\"search-banner\">\n" +
                "    <div class=\"container py-5 my-5\">\n" +
                "    <div class=\"row text-dark text-center pb-4\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <h2 id=\"respsearch\">Поиск</h2>\n" +
                "        </div>\n" +
                "    </div>   \n" +
                "    <div class=\"row\">\n" +
                "        <div class=\"col-md-12\">\n" +
                "            <div class=\"card\">\n" +
                "                <div class=\"card-body\">\n" +
                "                    <div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"datestart\" placeholder=\"Поиск с\" aria-label=\"Start date\">" +
                " <small class=\"form-text text-muted\">Дата регистрации, с которой будет производиться поиск</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                " <input class=\"form-control\" type=\"date\" id=\"dateend\" placeholder=\"Поиск до\" aria-label=\"End date\">" +
                " <small class=\"form-text text-muted\">Дата регистрации, до которой будет производиться поиск (Не включительно)</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"mail\" type=\"text\" placeholder=\"Почта пользователя\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "<div class=\"row\">\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"fname\" type=\"text\" placeholder=\"Фамилия пользователя\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"iname\" type=\"text\" placeholder=\"Имя пользователя\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <div class=\"form-group \">\n" +
                " <input class=\"form-control\" id=\"oname\" type=\"text\" placeholder=\"Отчество пользователя\">" +
                "                        </div>\n" +
                "                </div>\n" +
                "               <div class=\"col\">\n" +
                "                    <div style=\"margin-bottom: 0rem;\" class=\"form-group \">\n" +
                "                          <select id=\"inputRole\" class=\"form-control\" >\n" +
                "                            <option value=\"0\" selected>Неактивированный</option>\n" +
                "                            <option value=\"1\">Рабочий</option>\n" +
                "                            <option value=\"2\">Менеджер</option>\n" +
                "                            <option value=\"3\">Администратор</option>\n" +
                "                          </select>\n" +
                " <small class=\"form-text text-muted\">Роль</small>" +
                "                        </div>\n" +
                "                </div>\n" +
                "                <div class=\"col\">\n" +
                "                    <button type=\"button\" id=\"searchstartforadmin\" class=\"btn bg-main text-light\">Найти!</button>\n" +
                "                </div>\n" +
                "            </div>\n" +

                "                </div>\n" +
                "            </div>\n" +
                "            \n" +
                "        </div>\n" +
                "    </div>\n" +
                "<div id=\"respsearchbottom\"></div>" +
                "</div>\n" +
                "</section>";
        return searchBar;
    }

    public static String getCardsUserModel(User user) {
        String selected0 = "";
        String selected1 = "";
        String selected2 = "";
        String selected3 = "";
        switch (user.getUsertype()) {
            case 0:
                selected0 = "selected";
                break;
            case 1:
                selected1 = "selected";
                break;
            case 2:
                selected2 = "selected";
                break;
            case 3:
                selected3 = "selected";
                break;
        }

        List<User> ManagersUsers = UserManager.getUsertype(2L);
        String mainSelected = "";
        String selected = "";
        int count = 0;
        if (user.getUsertype() == 1 || user.getUsertype() == 0) {
            for (User userM : ManagersUsers) {
                String selectedManager = "";
                if (userM.getId() == user.getIdManager()) {selectedManager = "selected";count++;}
                selected += "<option "+selectedManager+" value=\"" + userM.getId() + "\">" + userM.getFname() + " " + userM.getIname() + " " + userM.getOname() + " (" + userM.getMail() + ")</option>";
            }
            if (count == 0) {selected += "<option value=\"0\" selected>Без менеджера</option>";} else {selected += "<option value=\"0\">Без менеджера</option>";}
            mainSelected = "       <div class=\"form-group \">\n" +
                    "          <label for=\"FormControl\">Управляющий менеджер</label>\n" +
                    "                          <select data-respinput=\"small8resp\" data-id=\""+user.getId()+"\" id=\"inputManagersForm"+user.getId()+"\" class=\"form-control\" >\n" + selected +
                    "                          </select>\n" +
                    "          <small class=\"form-text text-muted\" id=\"small8resp"+user.getId()+"\"></small>"+
                    "                        </div>\n";
        }
        String returnBack = "<div class=\"modal fade\" id=\"cardmodal" + user.getId() + "\" tabindex=\"-1\" aria-labelledby=\"ModalLabel\" aria-hidden=\"true\">\n" +
                "  <div class=\"modal-dialog modal-dialog-centered\">\n" +
                "    <div class=\"modal-content\">\n" +
                "      <div class=\"modal-header\">\n" +
                "        <h4 class=\"modal-title fs-5\">Карточка пользователя</h4>\n" +
                "      </div>\n" +
                "      <div class=\"modal-body\">\n" +
                "       <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">ID</label>\n" +
                "          <input type=\"text\" class=\"form-control\" value=\"" + user.getId() + "\" placeholder=\"\" disabled>\n" +
                "          <small class=\"form-text text-muted\" id=\"small1resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "       <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Почта</label>\n" +
                "          <input type=\"text\" data-respinput=\"small2resp\" data-id=\""+user.getId()+"\" id=\"changemail"+user.getId()+"\" class=\"form-control\" value=\"" + user.getMail() + "\" placeholder=\"Почта\">\n" +
                "          <small class=\"form-text text-muted\" id=\"small2resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Время регистрации</label>\n" +
                "          <input type=\"text\" class=\"form-control\" value=\"" + getInNormalDate(user.getTime()) + "\" placeholder=\"\" disabled>\n" +
                "          <small class=\"form-text text-muted\" id=\"small3resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Фамилия</label>\n" +
                "          <input data-respinput=\"small4resp\" type=\"text\" data-id=\""+user.getId()+"\" id=\"changefname"+user.getId()+"\" class=\"form-control\" value=\"" + user.getFname() + "\" placeholder=\"Фамилия\">\n" +
                "          <small class=\"form-text text-muted\" id=\"small4resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Имя</label>\n" +
                "          <input data-respinput=\"small5resp\" type=\"text\" data-id=\""+user.getId()+"\" id=\"changeiname"+user.getId()+"\" class=\"form-control\" value=\"" + user.getIname() + "\" placeholder=\"Имя\">\n" +
                "          <small class=\"form-text text-muted\" id=\"small5resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "        <div class=\"form-group\">\n" +
                "          <label for=\"FormControl\">Отчество</label>\n" +
                "          <input data-respinput=\"small6resp\" type=\"text\" data-id=\""+user.getId()+"\" id=\"changeoname"+user.getId()+"\" class=\"form-control\" value=\"" + user.getOname() + "\" placeholder=\"Отчество\">\n" +
                "          <small class=\"form-text text-muted\" id=\"small6resp"+user.getId()+"\"></small>"+
                "       </div>  " +
                "       <div class=\"form-group \">\n" +
                "          <label for=\"FormControl\">Роль</label>\n" +
                "                          <select onchange=\"if (this.value=='1') {$('#inputManagersForm"+user.getId()+"').prop('disabled', false);} else {$('#inputManagersForm"+user.getId()+"').prop('disabled', true);}\" data-respinput=\"small7resp\" data-id=\""+user.getId()+"\" id=\"inputRoleForm"+user.getId()+"\" class=\"form-control\" >\n" +
                "                            <option " + selected0 + " value=\"0\">Неактивированный</option>\n" +
                "                            <option " + selected1 + " value=\"1\">Рабочий</option>\n" +
                "                            <option " + selected2 + " value=\"2\">Менеджер</option>\n" +
                "                            <option " + selected3 + " value=\"3\">Администратор</option>\n" +
                "                          </select>\n" +
                "      <small class=\"form-text text-muted\" id=\"small7resp"+user.getId()+"\"></small>"+
                "                        </div>\n" + mainSelected +
                "      </div>\n" +
                "    </div>\n" +
                "  </div>\n" +
                "</div>";
        return returnBack;
    }
}
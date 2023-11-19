package com.example.servingwebcontent.func;

import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.managers.CardManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    public static String getCardsUser(Long idOwn) {
        List<Card> cards = CardManager.readAllByIdOwn(idOwn);
        String cardsHtmlBody = "";
        String cardsHtml = "<div class=\"container\"><div class=\"card-deck\">";
        for (Card card : cards) {
            cardsHtmlBody += "<div style=\"margin-bottom: 10px;\" class=\"card\">\n" +
                    "            <div class=\"card-body\">\n" +
                    "                <h5 class=\"card-title\">" + card.getName() + "</h5>\n" +
                    "                <p class=\"card-text\">Описание карточки</p>\n" +
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
                    "        ...\n" +
                    "      </div>\n" +
                    "      <div class=\"modal-footer\">\n" +
                    "        <button type=\"button\" class=\"btn btn-secondary\" data-dismiss=\"modal\">Close</button>\n" +
                    "        <button type=\"button\" class=\"btn btn-primary\">Save changes</button>\n" +
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
            cardsHtml = "<h1 class=\"display-4\">У вас нет ни одной карточки</h1>\n" +
                "<p><a href=\"/dashboard/upload\">Создайте свою первую карточку</a></p>";
        }
        return cardsHtml;
    }
}

package com.example.servingwebcontent.func;

import static com.example.servingwebcontent.func.MainFunction.*;

public class GetHeaders {
    public static String GetHead(int LevelInSystem) {
        String response = "";
        switch (LevelInSystem) {
            case 0:
                response = " <a class=\"navbar-brand desktop-nav\" href=\"#\">Облако</a>\n" +
                        "    <div class=\"navbar-nav desktop-nav ml-auto\">\n" +
                        "        <a href=\"../api/logout\" class=\"btn btn-outline-light my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>\n" +
                        "    <div class=\"mobile-nav d-lg-none\">\n" +
                        "        <a href=\"../api/logout\" class=\"btn btn-outline-light my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>";
                break;
            case 1:
                response = " <a class=\"navbar-brand desktop-nav\" href=\"#\">Облако</a>\n" +
                        "    <div class=\"navbar-nav desktop-nav ml-auto\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\">Главная</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/upload\">Загрузка</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/edit\">Редактирование</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/comments\">Комментарии</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\">Настройки</a>\n" +
                        "       <a href=\"../api/logout\" class=\"btn btn-outline-light my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>\n" +
                        "    <div class=\"mobile-nav d-lg-none\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\"><i class=\"fa-solid fa-house\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/upload\"><i class=\"fa-solid fa-cloud-arrow-up\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/edit\"><i class=\"fa-solid fa-pen-to-square\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/comments\"><i class=\"fa-solid fa-comment\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\"><i class=\"fa-solid fa-gears\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../api/logout\"><i class=\"fa-solid fa-right-from-bracket\" style=\"color:#fff;\"></i></a>\n" +
                        "    </div>";
                break;
            case 2:
                response = " <a class=\"navbar-brand desktop-nav\" href=\"#\">Облако</a>\n" +
                        "    <div class=\"navbar-nav desktop-nav ml-auto\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\">Главная</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/view\">Поиск и редактирование</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/createcard\">Создать карточку</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/newcomments\">Карточки на проверку</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\">Настройки</a>\n" +
                        "       <a href=\"../api/logout\" class=\"btn btn-outline-light my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>\n" +
                        "    <div class=\"mobile-nav d-lg-none\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\"><i class=\"fa-solid fa-house\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/view\"><i class=\"fa-solid fa-eye\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/createcard\"><i class=\"fa-solid fa-pen-to-square\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/newcomments\"><i class=\"fa-solid fa-check-circle\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\"><i class=\"fa-solid fa-gears\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../api/logout\"><i class=\"fa-solid fa-right-from-bracket\" style=\"color:#fff;\"></i></a>\n" +
                        "    </div>";
                break;
            case 3:
                response = " <a class=\"navbar-brand desktop-nav\" href=\"#\">Облако</a>\n" +
                        "    <div class=\"navbar-nav desktop-nav ml-auto\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\">Главная</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/searchusers\">Поиск и редактирование</a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\">Настройки</a>\n" +
                        "       <a href=\"../api/logout\" class=\"btn btn-outline-light my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>\n" +
                        "    <div class=\"mobile-nav d-lg-none\">\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard\"><i class=\"fa-solid fa-house\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/searchusers\"><i class=\"fa-solid fa-pen-to-square\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../dashboard/settings\"><i class=\"fa-solid fa-gears\" style=\"color:#fff;\"></i></a>\n" +
                        "       <a class=\"nav-item nav-link\" href=\"../api/logout\"><i class=\"fa-solid fa-right-from-bracket\" style=\"color:#fff;\"></i></a>\n" +
                        "    </div>";
                break;
            default:
                break;
        }
        return response;
    }
    public static String GetBody(int LevelInSystem, String page, Long idUser) {
        String response = "";
        switch (LevelInSystem) {
            case 0:
                response = "<h1 class=\"display-4\">Активируйте аккаунт</h1>\n" +
                        "    <p>Сообщите свою почту администратору</p>";
                break;
            case 1:
                switch (page) {
                    case "main": response = "<h1 class=\"display-4\">Здравствуйте!</h1>\n" +
                            "    <p>Тут пока ничего нет</p>";
                        break;
                    case "uploads": response = getCreateCardUser();
                        break;
                    case "edit": response = getSearchBarUserEdit(idUser);
                        break;
                    case "comments": response = getCommentsUser(idUser);
                        break;
                    case "settings": response = getSettingsUser(idUser);
                        break;
                    default:
                        break;
                }
                break;
            case 2:
                switch (page) {
                    case "main": response = "<h1 class=\"display-4\">Здравствуйте!</h1>\n" +
                            "    <p>Тут пока ничего нет</p>";
                        break;
                    case "view": response = getSearchBarManagerEdit(idUser);
                        break;
                    case "createcard": response = getCreateCardManager(idUser);
                        break;
                    case "newcomments": response = getCommentsForManager(idUser);
                        break;
                    case "settings":response = getSettingsUser(idUser);
                        break;
                }
            case 3:
                switch (page) {
                    case "main": response = "<h1 class=\"display-4\">Здравствуйте!</h1>\n" +
                            "    <p>Тут пока ничего нет</p>";
                        break;
                    case "searchusers":response = searchAndEdit(idUser);
                        break;
                    case "settings":response = getSettingsUser(idUser);
                        break;
                }
            default:
                break;
        }
        return response;
    }
    public static String GetFooter() {
        String response = "<div class=\"container\">\n" +
                "        <span class=\"text-light\">Облачное хранилище, версия 1.0.0</span>\n" +
                "    </div>";
        return response;
    }
}
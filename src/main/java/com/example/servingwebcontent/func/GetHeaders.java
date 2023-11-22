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
                    case "uploads": response =
                            "<h1 class=\"display-4\">Загрузка файлов</h1>  " +
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
                            "           <label for=\"fileUpload\" class=\"dropzone\" ondragover=\"onDragOver(event)\" ondrop=\"onDrop(event)\">\n" +
                            "               <input id=\"fileUpload\" type=\"file\" name=\"files\" multiple=\"multiple\" style=\"display: none;\" onchange=\"onFileSelect(event)\">\n" +
                            "               <span>Кликните или перетащите файлы сюда для загрузки</span>\n" +
                            "           </label>\n" +
                            "      </div>\n" +
                            "      <button type=\"submit\" id=\"uploadButton\" class=\"btn bg-main text-light\">Сохранить</button>\n";
                        break;
                    case "edit": response = getCardsUser(idUser);
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
                    case "view":
                        break;
                    case "createcard":
                        break;
                    case "newcomments":
                        break;
                    case "settings":response = getSettingsUser(idUser);
                        break;
                }
            case 3:
                switch (page) {
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

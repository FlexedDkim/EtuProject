package com.example.servingwebcontent.func;
public class GetHeaders {
    public static String GetHead(int LevelInSystem) {
        String response = "";
        switch (LevelInSystem) {
            case 0:
                response = "    <a class=\"navbar-brand\" href=\"#\">Облачное хранилище</a>\n" +
                        "    <div class=\"navbar-nav desktop-nav ml-auto\">\n" +
                        "        <a href=\"../api/logout\" class=\"btn btn-outline-danger my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>\n" +
                        "    <div class=\"mobile-nav d-lg-none\">\n" +
                        "        <a href=\"../api/logout\" class=\"btn btn-outline-danger my-2 my-sm-0 ml-2\" type=\"submit\">Выход</a>\n" +
                        "    </div>";
            break;
            default:
                break;
        }
        return response;
    }
    public static String GetBody(int LevelInSystem) {
        String response = "";
        switch (LevelInSystem) {
            case 0:
                response = "<h1 class=\"display-4\">Активируйте аккаунт</h1>\n" +
                           "    <p>Сообщите свою почту администратору</p>";
                break;
            default:
                break;
        }
        return response;
    }
    public static String GetFooter() {
        String response = "<div class=\"container\">\n" +
                "        <span class=\"text-muted\">Облачное хранилище, версия 1.0.0</span>\n" +
                "    </div>";
        return response;
    }
}

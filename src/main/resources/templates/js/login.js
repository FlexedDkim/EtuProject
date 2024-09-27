$(document).ready(function(){
    $('#btn_submit').click(function(){
        let mail   = $('#mail').val();
        let pass = $('#pass').val();
        $.ajax({
            url: "api/login",
            type: "post",
            data: {
                "mail":   mail,
                "pass":   pass
            },
            error:function(){$("#erconts").html("Ошибка авторизации!");},
            beforeSend: function() {
                $("#erconts").html("Авторизация...");
            },
            success: function(result){
                if (result === "Вход выполнен!") {
                    location.reload();
                }
                $('#erconts').html(result);
            }
        });
    });
});

$("#newpassbtn").on("click", function () {
    let pass   = $('#newpass').val();
    let passrepeat   = $('#newpassrepeat').val();
    let mail   = $('#mailcheck').val();
    $.ajax({
        url: "/api/nonnewpasscreate",
        type: "post",
        data: {
            "pass":   pass,
            "mail":   mail,
            "passrepeat":   passrepeat
        },
        error:function(){$("#newpassresp").html("Ошибка сброса пароля!");},
        beforeSend: function() {
            $("#newpassresp").html("Загрузка...");
        },
        success: function(result){
            if (result === "ok") {
                $('#modalRestorePass').modal('hide');
                $('#modalRestorePassCode').modal('show');
            }
            else
            {
                $("#newpassresp").html(result);
            }
        }
    });
});

$("#newcodepassbtn").on("click", function () {
    let code   = $('#newcodepass').val();
    $.ajax({
        url: "/api/nonnewpasscheck",
        type: "post",
        data: {
            "code":   code
        },
        error:function(){$("#newcodepassresp").html("Ошибка сброса пароля!");},
        beforeSend: function() {
            $("#newcodepassresp").html("Загрузка...");
        },
        success: function(result){
            if (result === "ok") {
                location.reload();
            }
            else
            {
                $("#newcodepassresp").html(result);
            }
        }
    });
});
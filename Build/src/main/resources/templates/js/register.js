$(document).ready(function(){
    $('#btn_submit').click(function(){
        var mail   = $('#mail').val();
        var pass = $('#pass').val();
        var captchainput = $('#captchainput').val();
        $.ajax({
            url: "api/register",
            type: "post",
            data: {
                "mail":   mail,
                "pass":   pass,
                "captcha":   captchainput
            },
            error:function(){$("#erconts").html("Ошибка регистрации!");},
            beforeSend: function() {
                $("#erconts").html("Регистрация...");
            },
            success: function(result){
                if (result == "Аккаунт зарегистрирован!") {
                    location.reload();
                }
                if (result == "Неверный код с картинки!") {
                    document.getElementById("captchainput").value = "";
                    document.getElementById('captcha').src='api/captcha?' + Math.random();
                }
                $('#erconts').html(result);
            }
        });
    });
});
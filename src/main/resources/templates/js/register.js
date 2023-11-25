$(document).ready(function(){
    $('#btn_submit').click(function(){
        let mail   = $('#mail').val();
        let pass = $('#pass').val();
        let captchainput = $('#captchainput').val();
        let pass_repeat = $('#pass_repeat').val();
        $.ajax({
            url: "api/register",
            type: "post",
            data: {
                "mail":   mail,
                "pass":   pass,
                "captcha":   captchainput,
                "passrepeat":   passrepeat
            },
            error:function(){$("#erconts").html("Ошибка регистрации!");},
            beforeSend: function() {
                $("#erconts").html("Регистрация...");
            },
            success: function(result){
                if (result === "Аккаунт зарегистрирован!") {
                    location.reload();
                }
                if (result === "Неверный код с картинки!") {
                    document.getElementById("captchainput").value = "";
                    document.getElementById('captcha').src='api/captcha?' + Math.random();
                }
                $('#erconts').html(result);
            }
        });
    });
    $('#btn_submit_refresh').click(function(){
        document.getElementById("captchainput").value = "";
        document.getElementById('captcha').src='api/captcha?' + Math.random();
    });
});
$(document).ready(function(){
    $('#btn_submit').click(function(){
        var mail   = $('#mail').val();
        var pass = $('#pass').val();
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
                if (result == "Вход выполнен!") {
                    location.reload();
                }
                $('#erconts').html(result);
            }
        });
    });
});
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
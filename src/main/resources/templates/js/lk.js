function onDragOver(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение браузера
}

function onDrop(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение браузера
    const files = event.dataTransfer.files; // Получаем перетаскиваемые файлы
    const fileInput = document.getElementById('fileUpload');
    fileInput.files = files; // Устанавливаем файлы в элемент input
    updateLabel(files); // Обновляем текст лейбла
}

function onFileSelect(event) {
    updateLabel(event.target.files); // Обновляем текст лейбла
}

function updateLabel(files) {
    const label = document.getElementById('fileUpload').nextElementSibling;
    const labelDefaultText = 'Кликните или перетащите файлы сюда для загрузки';
    label.textContent = files.length ? Array.from(files).map(f => f.name).join(', ') : labelDefaultText;
}

function submitComment(id,idarea,idcommarea) {
    let idtext = $('#' + idarea).val();
    $.ajax({
        url: "/api/createcomment",
        type: "post",
        data: {
            "idcard": id,
            "idtext": idtext
        },
        error: function () {
        },
        beforeSend: function () {
        },
        success: function (result) {
            if (result.status == "success") {
                let resultshow = `
    <div class="d-flex justify-content-center py-2">
        <div class="second py-2 px-2">
            <span class="text1">` + result.message + `</span>
            <div class="d-flex justify-content-between py-1 pt-2">
                <div>
                    <img src="../img/noavatar.png" width="20">
                    <span class="text2">` + result.fio + `</span>
                </div>
                <div>
                    <span class="text3">` + result.date + `</span>
                </div>
            </div>
        </div>
    </div>
`;
                $('#' + idcommarea).append(resultshow);
                $('#' + idarea).val("");
                $('#commentscounter' + id).html(result.count);
            }
        }
    });
}

$(document).ready(function () {
    $("#uploadButton").on("click", function () {
        var fileInput = $("#fileUpload")[0];
        var files = fileInput.files;

        if (files.length > 0) {
            uploadFiles(files);
        } else {
            $("#progressBar").text("Выберите один и более файлов для загрузки!");
        }
    });

    function uploadFiles(files) {
        var formData = new FormData();

        $.each(files, function(index, file) {
            formData.append("files", file);
        });

        var selectObject = $("#itemSelect").val();
        formData.append("selectObject", selectObject);

        var nameCard = $("#namecard").val();
        formData.append("namecard", nameCard);

        var descCard = $("#desccard").val();
        formData.append("desccard", descCard);

        $.ajax({
            url: "/api/uploadfiles",
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            xhr: function () {
                var xhr = new window.XMLHttpRequest();
                xhr.upload.addEventListener("progress", function (evt) {
                    if (evt.lengthComputable) {
                        var percentComplete = (evt.loaded / evt.total) * 100;
                        $("#progressBar").text(percentComplete.toFixed(2) + "%");
                    }
                }, false);
                return xhr;
            },
            success: function (data) {
                $("#progressBar").text(data);
            },
            error: function () {
                $("#progressBar").text("Ошибка загрузки файла.");
            }
        });
    }
    $("#newmailbtn").on("click", function () {
        let mail   = $('#newmail').val();
        $.ajax({
            url: "/api/newmailcreate",
            type: "post",
            data: {
                "mail":   mail
            },
            error:function(){$("#newmailresp").html("Ошибка сброса почты!");},
            beforeSend: function() {
                $("#newmailresp").html("Загрузка...");
            },
            success: function(result){
                if (result === "ok") {
                    $('#modalRestoreMail').modal('hide');
                    $('#modalRestoreMailCode').modal('show');
                }
                else
                {
                    $("#newmailresp").html(result);
                }
            }
        });
    });
    $("#newcodebtn").on("click", function () {
        let code   = $('#newcode').val();
        $.ajax({
            url: "/api/newmailcheck",
            type: "post",
            data: {
                "code":   code
            },
            error:function(){$("#newcoderesp").html("Ошибка сброса почты!");},
            beforeSend: function() {
                $("#newcoderesp").html("Загрузка...");
            },
            success: function(result){
                if (result === "ok") {
                    location.reload();
                }
                else
                {
                    $("#newcoderesp").html(result);
                }
            }
        });
    });

    $("#newpassbtn").on("click", function () {
        let pass   = $('#newpass').val();
        let passrepeat   = $('#newpassrepeat').val();
        $.ajax({
            url: "/api/newpasscreate",
            type: "post",
            data: {
                "pass":   pass,
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
            url: "/api/newpasscheck",
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
});
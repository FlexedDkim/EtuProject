function onDragOver(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение браузера
}

function onDrop(event) {
    event.preventDefault(); // Предотвращаем стандартное поведение браузера
    const files = event.dataTransfer.files; // Получаем перетаскиваемые файлы
    const id = event.target.dataset.myValue;
    const fileInput = document.getElementById('fileUpload' + id);
    fileInput.files = files; // Устанавливаем файлы в элемент input
    updateLabel(files,id); // Обновляем текст лейбла
}

function onFileSelect(event) {
    const id = event.target.dataset.myValue;
    updateLabel(event.target.files,id); // Обновляем текст лейбла
}

function updateLabel(files, id) {
    const label = document.getElementById('fileUpload' + id).nextElementSibling;
    const labelDefaultText = 'Кликните или перетащите файлы сюда для загрузки';
    label.textContent = files.length ? Array.from(files).map(f => f.name).join(', ') : labelDefaultText;
}

function submitComment(id,idarea,idcommarea) {
    var fileInput = $("#fileUpload" + id)[0];
    var files = fileInput.files;
    var formData = new FormData();

    $.each(files, function(index, file) {
        formData.append("files", file);
    });
    let idtext = $('#' + idarea).val();
    formData.append("idcard", id);
    formData.append("idtext", idtext);
    $.ajax({
        url: "/api/createcomment",
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
                $('#filecontainer' + id).append(result.card);
                const label = document.getElementById('fileUpload' + id).nextElementSibling;
                const labelDefaultText = 'Кликните или перетащите файлы сюда для загрузки';
                label.textContent = labelDefaultText;
                $("#fileUpload" + id).val(null);
            }
        },
        error: function () {
            $("#progressBar").text("Ошибка загрузки файла.");
        }
    });
}

function deleteFile(idfile) {
    $.ajax({
        url: "/api/deletefile",
        type: "post",
        data: {
            "idfile": idfile
        },
        error: function () {
        },
        beforeSend: function () {
        },
        success: function (result) {
            if (result.status == "success") {
                $('#namecard' + idfile).html(result.name);
                document.getElementById("btnDeletedFile" + idfile).disabled = true;
                document.getElementById("btnDownloadFile" + idfile).disabled = true;
            }
        }
    });
}

function onchangestatus(idcard) {
    let status = $('#itemStatusSelect' + idcard).val();
    $.ajax({
        url: "/api/onchangestatus",
        type: "post",
        data: {
            "idcard": idcard,
            "status": status
        },
        error: function () {
        },
        beforeSend: function () {
        },
        success: function (result) {
        }
    });
}
$(document).ready(function () {
    $("#uploadButton").on("click", function () {
        var fileInput = $("#fileUpload1")[0];
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

        if (document.getElementById('itemSelectEmployer')) {
            var itemSelectEmployer = $("#itemSelectEmployer").val();
            formData.append("itemSelectEmployer", itemSelectEmployer);
        }
        else
        {
            var itemSelectEmployer = $("#itemSelectEmployer").val();
            formData.append("itemSelectEmployer", 0);
        }

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
                if (data == "Файлы были успешно загружены!") {
                    $('#namecard').val("");
                    $('#desccard').val("");
                    $('#itemSelect').val("");

                    const label = document.getElementById('fileUpload1').nextElementSibling;
                    const labelDefaultText = 'Кликните или перетащите файлы сюда для загрузки';
                    label.textContent = labelDefaultText;
                    $("#fileUpload1").val(null);
                }
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

    $("#searchstart").on("click", function () {
        let namecard   = $('#namecard').val();
        let description = $('#descriptioncard').val();
        let inputStatus   = $('#inputStatus').val();
        let inputObject = $('#inputObject').val();
        let datestart = $('#datestart').val();
        let dateend = $('#dateend').val();
        let fnameauthorcard = $('#fnameauthorcard').val();
        let inameauthorcard = $('#inameauthorcard').val();
        let onameauthorcard = $('#onameauthorcard').val();
        $.ajax({
            url: "/api/searchengineuser",
            type: "post",
            data: {
                "name":   namecard,
                "description": description,
                "inputstatus": inputStatus,
                "inputobject": inputObject,
                "datestart": datestart,
                "dateend": dateend,
                "fnameauthorcard" : fnameauthorcard,
                "inameauthorcard" : inameauthorcard,
                "onameauthorcard" : onameauthorcard,
            },
            error:function(){$("#respsearch").html("Ошибка поиска");},
            beforeSend: function() {
                $("#respsearch").html("Поиск...");
            },
            success: function(result){
                $("#respsearch").html(result.messageup);
                $("#respsearchbottom").html(result.messagedown);
            }
        });
    });

    $("#searchstartformanager").on("click", function () {
        let namecard   = $('#namecard').val();
        let description = $('#descriptioncard').val();
        let inputStatus   = $('#inputStatus').val();
        let inputObject = $('#inputObject').val();
        let datestart = $('#datestart').val();
        let dateend = $('#dateend').val();
        let fnameauthorcard = $('#fnameauthorcard').val();
        let inameauthorcard = $('#inameauthorcard').val();
        let onameauthorcard = $('#onameauthorcard').val();
        let fnameexcard = $('#fnameexcard').val();
        let inameexcard = $('#inameexcard').val();
        let onameexcard = $('#onameexcard').val();
        $.ajax({
            url: "/api/searchenginemanager",
            type: "post",
            data: {
                "name":   namecard,
                "description": description,
                "inputstatus": inputStatus,
                "inputobject": inputObject,
                "datestart": datestart,
                "dateend": dateend,
                "fnameauthorcard" : fnameauthorcard,
                "inameauthorcard" : inameauthorcard,
                "onameauthorcard" : onameauthorcard,
                "fnameexcard" : fnameexcard,
                "inameexcard" : inameexcard,
                "onameexcard" : onameexcard
            },
            error:function(){$("#respsearch").html("Ошибка поиска");},
            beforeSend: function() {
                $("#respsearch").html("Поиск...");
            },
            success: function(result){
                $("#respsearch").html(result.messageup);
                $("#respsearchbottom").html(result.messagedown);
            }
        });
    });

    $("#searchstartforadmin").on("click", function () {
        let inputRole = $('#inputRole').val();
        let datestart = $('#datestart').val();
        let dateend = $('#dateend').val();
        let fname = $('#fname').val();
        let iname = $('#iname').val();
        let oname= $('#oname').val();
        let mail= $('#mail').val();
        $.ajax({
            url: "/api/searchengineadmin",
            type: "post",
            data: {
                "inputrole": inputRole,
                "datestart": datestart,
                "dateend": dateend,
                "fname" : fname,
                "iname" : iname,
                "oname" : oname,
                "mail" : mail
            },
            error:function(){$("#respsearch").html("Ошибка поиска");},
            beforeSend: function() {
                $("#respsearch").html("Поиск...");
            },
            success: function(result){
                $("#respsearch").html(result.messageup);
                $("#respsearchbottom").html(result.messagedown);
                const inputs = document.querySelectorAll('input, select');
                inputs.forEach(setupInput);
            }
        });
    });
});

function saveData(inputId, value,id) {
    $.ajax({
        url: "/api/savedataadmin",
        type: "post",
        data: {
            "iduser":   id,
            "inputid": inputId,
            "value": value,
        },
        error:function(){$("#" +inputId + id).html("Ошибка сохранения значения");},
        beforeSend: function() {
            $("#" +inputId + id).html("Сохранение...");
        },
        success: function(result){
            $("#" +inputId + id).html(result);
        }
    });
}

function setupInput(input) {
    let timeoutId = null;

    input.addEventListener('input', function (event) {
        const inputId = event.target.id;
        const value = event.target.value;
        const dataid = event.target.getAttribute('data-id');
        if (dataid) {
            if (timeoutId) {
                clearTimeout(timeoutId);
            }
            timeoutId = setTimeout(() => {
                saveData(inputId, value, dataid);
            }, 1000);
        }
    });
}
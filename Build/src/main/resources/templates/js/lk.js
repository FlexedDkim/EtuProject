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
});
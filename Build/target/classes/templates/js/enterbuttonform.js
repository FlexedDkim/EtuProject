function handleEnterKeyPress(event) {
    if (event.keyCode === 13) {
        $("#btn_submit").click();
    }
}

document.addEventListener("keydown", handleEnterKeyPress);
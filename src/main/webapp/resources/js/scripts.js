function loadStatusFixed() {
    $('#modal_loading').modal({
        backdrop: 'static',
        keyboard: true,
        show: true
    });
}

function loadStatusFixedClose() {
    $('#modal_loading').modal({
        backdrop: 'static',
        keyboard: false,
        show: false
    });
}

function loadSelecionarUsuarioMensagemFixed() {
    $('#modal_selecionar_usuario').modal({
        backdrop: 'static',
        keyboard: true,
        show: true
    });
}

function click_salvando_aposta(btnx) {
    var btn = btnx;
    btn.value = 'SALVANDO ...';
    btn.disabled = true;
    setTimeout(function () {
        null;
        ////btn.input('reset');
        //btn.reset();
    }, 1500);
}

$(document).ready(function () {
    $('.disabled').on('click', function (e) {
        e.preventDefault();
        return false;
    });
});

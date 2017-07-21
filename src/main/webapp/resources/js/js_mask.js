$(document).ready(load_masks);

function load_masks() {
    $('.mask_data').mask('00/00/0000', {clearIfNotMatch: true});
    $('.mask_hora').mask('00:00', {clearIfNotMatch: true});
}
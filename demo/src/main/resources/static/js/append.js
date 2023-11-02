$(function() {
    var appendBtn = document.getElementById("appendBtn")
    $("#appendBtn").click(function() {
        $("#tasksList").append('<li><input type="text" name="documentTitle" placeholder="문서명"><button class="delete">삭제</button></li>')
    })
});

$(".delete").click(function() {
    var parentNode = $(this).parent();
    $parent.css("animation", "fadeOut .3s linear");
    parentNode.remove();
})
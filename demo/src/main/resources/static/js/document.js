$.fn.ashCordian = function() {

    var container = $(this);

    container.find('header').click(function() {
        if($(this).siblings('section').css('display') == 'block'){
            container.find('section').slideUp(150);
        } else {
            container.find('section').slideUp(150);
            $(this).siblings('section').slideDown(150);
        }
    });
};


$('#accord1').ashCordian();

const imageInput = document.getElementById("imageInput");
const videoInput = document.getElementById("videoInput");

imageInput.addEventListener('change',function(e){

    var files = e.target.files;

    var formData = new FormData();

    formData.append('uploadFile', files[0]);

    $.ajax({
            url: "/fileUpload",
            type: "POST",
            contentType: false,
            processData: false,
            data: formData,
            success:
            function(result){
                alert("저장 성공");
                getImage();
            }
    });
});

videoInput.addEventListener('change',function(e){

    var files = e.target.files;

    var formData = new FormData();

    formData.append('uploadFile', files[0]);

    $.ajax({
            url: "/fileUpload",
            type: "POST",
            contentType: false,
            processData: false,
            data: formData,
            success:
            function(result){
                alert("저장 성공");
                getVideo();
            }
    });
});

function getImage(){
    $.ajax({
        type: 'GET',
        url: '/fileName/return',
        dataType: 'text',
        success: function(result) {
            makeImage(result);
        }
    });
}

function getVideo(){
    $.ajax({
        type: 'GET',
        url: '/fileName/return',
        dataType: 'text',
        success: function(result) {
            makeVideo(result);
        }
    });
}

function makeImage(imageName){
    let new_block = document.createElement('div');

    new_block.setAttribute('id', 'blockImage');
    new_block.setAttribute('class', 'block');
    new_block.setAttribute('content', imageName);

    let imageEl = document.createElement('img');

    imageEl.setAttribute('class', 'block_image');
    imageEl.setAttribute('src', imageName);
    imageEl.setAttribute('ondragstart', 'return false');

    new_block.appendChild(imageEl);

    createMoveBlock(new_block)
}

function makeVideo(videoName){
    let new_block = document.createElement('div');

    new_block.setAttribute('id', 'blockVideo');
    new_block.setAttribute('class', 'block');
    new_block.setAttribute('align', 'center');
    new_block.setAttribute('content', videoName);

    let videoEl = document.createElement('video');

    videoEl.setAttribute('src', videoName);
    videoEl.setAttribute('controls', '');
    videoEl.setAttribute('class', 'block_video');
    videoEl.setAttribute('ondragstart', 'return false');

    new_block.appendChild(videoEl);

    createMoveBlock(new_block);
}

const uploadImage = document.getElementById("image-btn");
const uploadVideo = document.getElementById("video-btn");

uploadImage.addEventListener('click', () => imageInput.click());
uploadVideo.addEventListener('click', () => videoInput.click());

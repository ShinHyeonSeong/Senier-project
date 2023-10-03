const postTitles = document.querySelectorAll(".post h2 a");
postTitles.forEach((title) => {
	title.addEventListener("click", (event) => {
		event.preventDefault();
		const postUrl = title.getAttribute("href");
		window.location.href = postUrl;
	});
});

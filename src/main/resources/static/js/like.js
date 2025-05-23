document.addEventListener("DOMContentLoaded", function () {
    const likeForm = document.getElementById('likeForm');
    console.log("outside 1", likeForm)
    if (likeForm) {
    console.log("inside 1")
        likeForm.addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(likeForm);

            fetch(likeForm.action, {
                method: "POST",
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                const likeButton = likeForm.querySelector("button");
                const icon = likeButton.querySelector("i");
                const textSpan = likeButton.querySelector("span");
                const countSpan = document.querySelector("#likes-count");

                if (data.success) {
                    if(data.data.liked){
                        likeButton.classList.remove("btn-outline-primary");
                        likeButton.classList.add("btn-primary");
                        icon.classList.add("text-white");
                        textSpan.textContent = "Liked";
                    } else {
                        likeButton.classList.remove("btn-primary");
                        likeButton.classList.add("btn-outline-primary");
                        icon.classList.remove("text-white");
                        textSpan.textContent = "Like";
                    }
                    if (countSpan) {
                       countSpan.textContent = `${data.data.likesCount} likes`;
                    }
                } else {
                    likeButton.classList.remove("btn-primary");
                    likeButton.classList.add("btn-outline-primary");
                    icon.classList.remove("text-white");
                    textSpan.textContent = "Like";
                    showToast(data.message, 'bg-danger')
                }
            })
            .catch(error => console.error("Error:", error));
        });
    }

    function showToast(message, status = 'bg-danger') {
        const toast = document.getElementById('liveToast');
        const toastMessage = document.getElementById('toastMessage');
        const toastWrapper = document.getElementById('toastWrapper');

        if (toast && toastMessage && toastWrapper) {
            toastMessage.textContent = message;
            toast.className = `toast align-items-center text-white border-0 ${status}`;
            toastWrapper.style.display = 'block';
            const bootstrapToast = new bootstrap.Toast(toast, { delay: 3000 });
            bootstrapToast.show();
        }
    }
});


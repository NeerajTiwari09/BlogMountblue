document.addEventListener("DOMContentLoaded", function () {
    const likeForm = document.getElementById('likeForm');
    if (likeForm) {
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
                const countSpan = document.querySelector("#likes-count");

                if (data.success) {
                    if(data.data.liked){
                        icon.classList.add("text-primary");
                        icon.classList.remove("text-muted");
                    } else {
                        icon.classList.remove("text-primary");
                        icon.classList.add("text-muted");
                    }
                    if (countSpan) {
                       countSpan.textContent = formatLikeCount(data.data.likesCount);
                    }
                } else {
                    showToast(data.message, 'bg-danger')
                }
            })
            .catch(error => showToast('Something went wrong!', 'bg-danger'));
        });
    }

    const likedUserForm = document.getElementById('likedUserForm');
    if (likedUserForm) {
        likedUserForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const formData = new FormData(likedUserForm);
            fetch(likedUserForm.action, {
                method: "POST",
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.data) {
                    const users = data.data;
                    const container = document.getElementById('likedUsersContainer');
                    container.innerHTML = '';
                    users.forEach(user => {
                        const userDiv = document.createElement('div');
                        userDiv.className = 'd-flex justify-content-between align-items-center mb-2';
                        if (!user.imageUrl) {
                            userDiv.innerHTML = `
                                <i class="bi bi-person-circle fs-1 d-inline-flex align-items-center justify-content-center"
                                   style="height: 50px; width: 50px; object-fit: cover;"></i>
                                <strong>${user.name}</strong>
                            `;
                        } else {
                            userDiv.innerHTML = `
                                <img src="${user.imageUrl}" alt="Profile Image"
                                     class="rounded-circle border"
                                     style="height: 50px; width: 50px; object-fit: cover;" />
                                <strong>${user.name}</strong>
                            `;
                        }
                        container.appendChild(userDiv);
                    });
                    const modal = new bootstrap.Modal(document.getElementById('likedByUsersModal'));
                    modal.show();
                } else {
                    showToast(data.message, 'bg-danger')
                }
            })
            .catch(error => showToast('Something went wrong!', 'bg-danger'));
        });
    }
});
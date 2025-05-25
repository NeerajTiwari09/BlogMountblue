let selectedImageFile = null;
document.addEventListener("DOMContentLoaded", function () {

    const profileUpdateForm = document.getElementById('profileUpdateForm');
    if (profileUpdateForm) {
        profileUpdateForm.addEventListener("submit", function (event) {
            event.preventDefault();
            updateProfile(profileUpdateForm);
        });
    }

    const changePasswordForm = document.getElementById("changePasswordForm");
    if (changePasswordForm) {
        changePasswordForm.addEventListener("submit", function (event) {
            event.preventDefault();
            const currentPassword = document.getElementById("currentPassword").value;
            const newPassword = document.getElementById("newPassword").value;
            const confirmPassword = document.getElementById("confirmPassword").value;
            changePassword(changePasswordForm, currentPassword, newPassword, confirmPassword);
        });
    }

    const changePasswordFormMobile = document.getElementById("changePasswordFormMobile");
    if (changePasswordFormMobile) {
        changePasswordFormMobile.addEventListener("submit", function (event) {
            event.preventDefault();
            const currentPassword = document.getElementById("currentPasswordSm").value;
            const newPassword = document.getElementById("newPasswordSm").value;
            const confirmPassword = document.getElementById("confirmPasswordSm").value;
            changePassword(changePasswordFormMobile, currentPassword, newPassword, confirmPassword);
        });
    }
});

function previewModalImage(event) {
    const file = event.target.files[0];
    const previewImg = document.getElementById('modalImagePreview');
    const container = document.getElementById('modalImagePreviewContainer');
    if (file) {
        selectedImageFile = file;
        const reader = new FileReader();
        reader.onload = function (e) {
            previewImg.src = e.target.result;
            container.classList.remove('d-none');
            container.classList.add('d-flex', 'justify-content-center');
        };
        reader.readAsDataURL(file);
    } else {
        selectedImageFile = null;
        container.classList.add('d-none');
        container.classList.remove('d-flex', 'justify-content-center');
    }
}

function updateProfile(profileUpdateForm) {
    const formData = new FormData(profileUpdateForm);
    formData.append('image', selectedImageFile);
    fetch(profileUpdateForm.action, {
        method: "POST",
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            document.getElementById('profileImage').src = data.data.imageUrl;
            document.getElementById('username').src = data.data.name;
            const modal = bootstrap.Modal.getInstance(document.getElementById('imageModal'));
            modal.hide();
            showToast(data.message, 'bg-success')
        } else {
            showToast(data.message, 'bg-danger')
        }
    })
    .catch(error => showToast('Something went wrong!', 'bg-danger'));
}


function changePassword(changePasswordForm, currentPassword, newPassword, confirmPassword) {
    if (newPassword !== confirmPassword) {
        showToast("New password and confirmation do not match.", "bg-danger");
        return;
    }
    const requestBody = {
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword
    };
    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    fetch(changePasswordForm.action, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [header]: token
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            const modal = bootstrap.Modal.getInstance(document.getElementById("changePasswordModal"));
            if (modal) {
                modal.hide();
            }
            showToast(data.message, "bg-success");
            changePasswordForm.reset();
        } else {
            showToast(data.message, "bg-danger");
        }
    })
    .catch(error => {
        showToast("Something went wrong!", "bg-danger");
    });
}

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

  document.addEventListener("DOMContentLoaded", function () {
    const navbar = document.querySelector('.navbar.fixed-top');
    if (navbar) {
      const height = navbar.offsetHeight;
      document.body.style.paddingTop = height + 'px';
    }
  });
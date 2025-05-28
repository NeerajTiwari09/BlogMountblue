window.addEventListener('DOMContentLoaded', () => {
  const toastElement = document.getElementById('liveToast');
  const toastMessage = document.getElementById('toastMessage');
  if (toastElement && toastMessage && toastMessage.textContent.trim().length > 0) {
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();
  }
});
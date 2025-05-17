window.addEventListener('DOMContentLoaded', () => {
  const toastElement = document.getElementById('liveToast');
  if (toastElement) {
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();
  }
});

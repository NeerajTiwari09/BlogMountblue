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

    document.querySelectorAll('.likes-count').forEach(el => {
        const rawCount = parseInt(el.dataset.rawCount);
        el.textContent = formatLikeCount(rawCount);
    });
});

function formatLikeCount(count) {
    if (count >= 1_000_000) {
        return (count / 1_000_000).toFixed(count % 1_000_000 === 0 ? 0 : 1) + 'M';
    } else if (count >= 1_000) {
        return (count / 1_000).toFixed(count % 1_000 === 0 ? 0 : 1) + 'k';
    }
    return count.toString();
}
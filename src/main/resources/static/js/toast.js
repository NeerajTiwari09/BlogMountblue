window.addEventListener('DOMContentLoaded', () => {
  const toastElement = document.getElementById('liveToast');
  const toastMessage = document.getElementById('toastMessage');
  if (toastElement && toastMessage && toastMessage.textContent.trim().length > 0) {
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();
  }
  const filterElement = document.getElementById("filterForm");
  if(filterElement)
    {
     filterElement.addEventListener("submit", function (e) {
        const selectedTagIds = Array.from(document.querySelectorAll(".tag-checkbox:checked"))
          .map(cb => cb.value)
          .join(",");
          console.log(selectedTagIds);
        document.getElementById("tags-input").value = selectedTagIds;
     });
  }
});



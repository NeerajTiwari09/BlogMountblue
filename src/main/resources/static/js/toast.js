window.addEventListener('DOMContentLoaded', () => {
  const toastElement = document.getElementById('liveToast');
  if (toastElement) {
    const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
    toast.show();
  }
  console.log("DOM fully loaded1");
  document.getElementById("filterForm").addEventListener("submit", function (e) {
  console.log("DOM fully loaded2");
      const selectedTagIds = Array.from(document.querySelectorAll(".tag-checkbox:checked"))
          .map(cb => cb.value)
          .join(",");
          console.log(selectedTagIds);
      document.getElementById("tags-input").value = selectedTagIds;
  });
});



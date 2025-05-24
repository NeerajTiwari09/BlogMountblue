window.addEventListener('DOMContentLoaded', function () {
        const content = document.getElementById('markdown-content').textContent;

        new toastui.Editor.factory({
            el: document.querySelector('#viewer'),
            viewer: true,
            height: '400px',
            initialValue: content
        });
    });
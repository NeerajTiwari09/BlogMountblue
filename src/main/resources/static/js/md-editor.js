const editor = new toastui.Editor({
        el: document.querySelector('#editor'),
        height: '470px',
        initialEditType: 'markdown',
        previewStyle: 'vertical',
        initialValue: document.querySelector('#content').value,
        hooks: {
            addImageBlobHook: (blob, callback) => {
                // You can implement image uploading logic here if needed
            }
        }
    });
    function syncMarkdown() {
        document.querySelector('#content').value = editor.getMarkdown();
    }
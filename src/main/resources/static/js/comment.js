let currentPage = 1;
let isLoading = false;
let hasMoreLikeOrComment = true
let currentPostId = null;
let observerAttached = false;
let currentUrl = `/blog/comment/api`
let currentElementBody = 'commentsPanelBody'
let currentElementPanel = 'commentsPanel'
const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

function openCommentsPanel(page, postId) {
    currentPage = page;
    currentPostId = postId;
    isLoading = false;

    const panelBody = document.getElementById(currentElementBody);
    panelBody.innerHTML = '';

//    const offcanvasElement = document.getElementById(currentElementPanel);
//    const offcanvas = new bootstrap.Offcanvas(offcanvasElement);
//    offcanvas.show();

    loadMoreComments();

    if (!observerAttached) {
        panelBody.addEventListener('scroll', handleScroll);
        observerAttached = true;
    }
}

// Load comments via AJAX
function loadMoreComments() {
    if (isLoading || currentPostId === null || !hasMoreLikeOrComment) return;
    isLoading = true;

    let commentRequest = {offset: currentPage, query: {postId : currentPostId}}
    fetch(currentUrl, {
        method: 'POST',
        body: JSON.stringify(commentRequest),
        headers: {
           "Content-Type": "application/json",
           [header]: token
        }
    })
    .then(response => {
        if (!response.ok) throw new Error('Failed to fetch');
        return response.text();
    })
    .then(html => {
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = html;
        const emptyFragment = tempDiv.querySelector('#empty-fragment');
        if (emptyFragment) {
            const message = emptyFragment.getAttribute('data-message') || "User needs to be logged in.";
            showToast(message, 'bg-danger')
            observerAttached = false;
            hasMoreLikeOrComment = false
            return;
        }
        const offcanvasElement = document.getElementById(currentElementPanel);
        const offcanvas = new bootstrap.Offcanvas(offcanvasElement);
        if (!offcanvasElement.classList.contains('show')) {
            offcanvas.show();
        }
        const panelBody = document.getElementById(currentElementBody);
        panelBody.insertAdjacentHTML('beforeend', html);
        currentPage++;
    })
    .catch(error => {
        console.error('Error loading comments:', error);
        showToast(error.message, 'bg-danger');
    })
    .finally(() => {
        isLoading = false;
    });
}

function handleScroll() {
    const panel = document.getElementById(currentElementBody);
    if (panel.scrollTop + panel.clientHeight >= panel.scrollHeight - 100) {
        loadMoreComments();
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const commentButton = document.getElementById('open-comments');
    if(commentButton) {
        commentButton.addEventListener('click', function () {
            currentUrl = `/blog/comment/api`
            currentElementBody = 'commentsPanelBody'
            currentElementPanel = 'commentsPanel'
            observerAttached = false;
            hasMoreLikeOrComment = true
            const postId = this.getAttribute('data-post-id');
            if (postId) {
                openCommentsPanel(1, postId);
            }
        });
    }

    const likeCountButton = document.getElementById('open-likes');
    if(likeCountButton) {
        likeCountButton.addEventListener('click', function () {
            currentUrl = `/blog/like/liked-by`
            currentElementBody = 'likesPanelBody'
            currentElementPanel = 'likesPanel'
            observerAttached = false;
            hasMoreLikeOrComment = true
            const postId = this.getAttribute('data-post-id');
            if (postId) {
                openCommentsPanel(1, postId);
            }
        });
    }
});

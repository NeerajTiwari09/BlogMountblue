const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
const headers = {};
headers[csrfHeaderName] = csrfToken;

const socket = new SockJS('/blog/ws');
const stompClient = Stomp.over(socket);
stompClient.connect(headers, function (frame) {
    stompClient.subscribe('/user/queue/notifications', function (message) {
        const notif = JSON.parse(message.body);
        showToast('🔔 You received a new Notification.', 'bg-success');
        const badgeElements = document.querySelectorAll('.notification-badge');
        if (badgeElements.length > 0) {
            badgeElements.forEach(badge => {
                badge.innerText = parseInt(badge.innerText || '0') + 1;
            });
        } else {
            document.querySelectorAll('.fa-bell').forEach(icon => {
                let newBadge = document.createElement('span');
                newBadge.className = 'translate-middle badge rounded-pill bg-danger notification-badge';
                newBadge.innerText = '1';
                icon.after(newBadge);
            });
        }

        const li = document.createElement('li');
        const a = document.createElement('a');
        a.className = 'dropdown-item small text-wrap';
        a.style.backgroundColor = '#ffeeba';
        a.style.color = '#212529';
        a.innerText = notif.message;
        a.href = '/notifications/' + notif.id + '/read';
        li.appendChild(a);

        // Prepend to both notification lists (desktop & mobile)
        const desktopList = document.getElementById('notificationList');
        const mobileList = document.getElementById('notificationListMobile');
        if (desktopList) desktopList.prepend(li.cloneNode(true));
        if (mobileList) mobileList.prepend(li.cloneNode(true));
    });
});

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.notification-link').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const notifId = this.getAttribute('data-id');
            const urlToRedirect = this.getAttribute('data-url');
            const element = this;

            fetch(`/blog/notifications/${notifId}/read`, {
                method: 'GET',
            })
            .then(response => response.json())
            .then(response => {
                if (response.success) {
                    if (!response.data.alreadySeen) {
//                      since we have 2 badges
                        let badgeElements = document.querySelectorAll('.notification-badge');
                        if (badgeElements.length > 0) {
                            badgeElements.forEach(badge => {
                                badge.innerText = parseInt(badge.innerText || '0') - 1;
                                if(badge.innerText <= 0) {
                                    badge.remove();
                                }
                            });
                        }
                        document.querySelectorAll(`.notification-link[data-id="${notifId}"]`).forEach(el => {
                            el.style.backgroundColor = '';
                            el.style.color = '';
                            el.style.fontWeight = 'normal';
                        });
                    }
                    if (urlToRedirect) {
                        window.location.href = urlToRedirect;
                    }
                } else {
                    showToast('Failed to mark notification as read', 'bg-danger');
                }
            }).catch(error => {
                showToast('Something went wrong!', 'bg-danger');
            });
        });
    });
});

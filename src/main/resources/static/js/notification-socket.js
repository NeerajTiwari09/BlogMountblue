const socket = new SockJS('/blog/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({}, function (frame) {
    stompClient.subscribe('/user/queue/notifications', function (message) {
        const notif = JSON.parse(message.body);
        showToast('🔔 You received a new Notification.', 'bg-success');
        let badge = document.querySelector('.fa-bell + .badge');
        if (badge) {
            badge.innerText = parseInt(badge.innerText || '0') + 1;
        } else {
            let newBadge = document.createElement('span');
            newBadge.className = 'badge bg-danger';
            newBadge.innerText = '1';
            document.querySelector('.fa-bell').after(newBadge);
        }

        // Add the notification to the dropdown
        const li = document.createElement('li');
        const a = document.createElement('a');
        a.className = 'dropdown-item small text-wrap';
        a.style.backgroundColor = '#ffeeba'
        a.style.color = '#212529'
        a.innerText = notif.message;
        a.href = '/notifications/' + notif.id + '/read';
        li.appendChild(a);
        document.getElementById('notificationList').prepend(li);
    });
});

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.notification-link').forEach(link => {
        link.addEventListener('click', function (e) {
            e.preventDefault();
            const notifId = this.getAttribute('data-id');
            const element = this;

            fetch(`/blog/notifications/${notifId}/read`, {
                method: 'GET',
            })
            .then(response => response.json())
            .then(response => {
                if (response.success) {
                    let badge = document.querySelector('.fa-bell + .badge');
                    if (badge) {
                        badge.innerText = parseInt(badge.innerText || '0') - 1;
                        if(badge.innerText <= 0) {
                            badge.remove();
                        }
                    }
                    element.style.backgroundColor = '';
                    element.style.color = '';
                    element.style.fontWeight = 'normal';
                } else {
                    showToast('Failed to mark notification as read', 'bg-danger');
                }
            }).catch(error => {
                showToast('Something went wrong!', 'bg-danger');
            });
        });
    });
});

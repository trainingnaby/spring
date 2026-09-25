(function () {
    const status = document.getElementById('ws-status');
    const list = document.getElementById('ws-notifications');

    if (!status || !list) {
        return;
    }

    function ajouterNotification(notification) {
        const empty = list.querySelector('.empty');
        if (empty) {
            empty.remove();
        }

        const item = document.createElement('li');
        item.className = notification.type === 'SUPPRESSION' ? 'ws-item suppression' : 'ws-item creation';

        const date = notification.dateNotification ? new Date(notification.dateNotification).toLocaleTimeString() : '';
        item.innerHTML = '<strong>' + notification.type + '</strong> '
            + '<span>' + notification.message + '</span>'
            + '<small>' + date + '</small>';

        list.prepend(item);
    }

    const socket = new SockJS('/ws-duplicatas');
    const stompClient = Stomp.over(socket);

    // Moins de bruit dans la console du navigateur pendant la formation.
    stompClient.debug = null;

    stompClient.connect({}, function () {
        status.textContent = 'Connecté aux notifications temps réel';
        status.className = 'badge success';

        stompClient.subscribe('/topic/duplicatas', function (message) {
            ajouterNotification(JSON.parse(message.body));
        });
    }, function () {
        status.textContent = 'Connexion WebSocket indisponible';
        status.className = 'badge error';
    });
})();

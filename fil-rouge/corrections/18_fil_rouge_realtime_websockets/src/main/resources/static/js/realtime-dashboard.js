let stompClient = null;
let reconnectTimer = null;
let reconnectAttempts = 0;

const statusEl = document.getElementById('realtime-status');
const pingButton = document.getElementById('ping-button');
const refreshSessionsButton = document.getElementById('refresh-sessions');

function setStatus(text, cssClass) {
    statusEl.textContent = text;
    statusEl.className = 'badge ' + (cssClass || '');
}

function formatTime(value) {
    if (!value) {
        return new Date().toLocaleTimeString();
    }
    return new Date(value).toLocaleTimeString();
}

function prependItem(listId, html) {
    const list = document.getElementById(listId);
    const empty = list.querySelector('.empty');
    if (empty) {
        empty.remove();
    }
    const li = document.createElement('li');
    li.innerHTML = html;
    list.prepend(li);

    while (list.children.length > 12) {
        list.lastElementChild.remove();
    }
}

function updateStats(stats) {
    document.getElementById('connected-users').textContent = stats.connectedUsers ?? 0;
    document.getElementById('total-connections').textContent = stats.totalConnections ?? 0;
    document.getElementById('total-disconnections').textContent = stats.totalDisconnections ?? 0;
    document.getElementById('messages-sent').textContent = stats.messagesSent ?? 0;
}

function connect() {
    clearTimeout(reconnectTimer);
    setStatus(reconnectAttempts === 0 ? 'Connexion en cours...' : 'Reconnexion en cours...', 'pending');

    const socket = new SockJS('/ws-duplicatas');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function () {
        reconnectAttempts = 0;
        setStatus('Connecté', 'success');

        stompClient.subscribe('/topic/duplicatas', function (message) {
            const event = JSON.parse(message.body);
            prependItem('duplicata-events', `<strong>${event.type}</strong> — ${event.message}<br><small>${formatTime(event.timestamp || event.dateNotification)}</small>`);
        });

        stompClient.subscribe('/topic/system', function (message) {
            const event = JSON.parse(message.body);
            prependItem('system-events', `<strong>${event.type}</strong> — ${event.message}<br><small>${formatTime(event.timestamp || event.dateNotification)}</small>`);
        });

        stompClient.subscribe('/topic/statistiques', function (message) {
            updateStats(JSON.parse(message.body));
        });

        stompClient.subscribe('/user/queue/notifications', function (message) {
            const notification = JSON.parse(message.body);
            prependItem('private-notifications', `<strong>${notification.title}</strong> — ${notification.message}<br><small>${formatTime(notification.timestamp)}</small>`);
        });

        stompClient.send('/app/dashboard/stats', {}, JSON.stringify({}));
        loadSessions();
    }, function () {
        scheduleReconnect();
    });

    socket.onclose = function () {
        if (!stompClient || !stompClient.connected) {
            scheduleReconnect();
        }
    };
}

function scheduleReconnect() {
    reconnectAttempts++;
    const delay = Math.min(5000 * reconnectAttempts, 30000);
    setStatus(`Connexion perdue. Reconnexion dans ${delay / 1000}s...`, 'error');
    clearTimeout(reconnectTimer);
    reconnectTimer = setTimeout(connect, delay);
}

function sendPing() {
    if (stompClient && stompClient.connected) {
        stompClient.send('/app/dashboard/ping', {}, JSON.stringify({ source: 'dashboard' }));
    } else {
        prependItem('system-events', '<strong>CLIENT</strong> — Impossible d’envoyer le ping : WebSocket non connecté.');
    }
}

async function loadSessions() {
    try {
        const response = await fetch('/api/ws-monitoring/sessions');
        if (!response.ok) {
            throw new Error('HTTP ' + response.status);
        }
        const sessions = await response.json();
        const list = document.getElementById('sessions-list');
        list.innerHTML = '';
        if (sessions.length === 0) {
            list.innerHTML = '<li class="empty">Aucune session connue.</li>';
            return;
        }
        sessions.forEach(session => {
            const li = document.createElement('li');
            li.innerHTML = `<strong>${session.username}</strong><br><small>${session.sessionId} — connecté à ${formatTime(session.connectedAt)}</small>`;
            list.appendChild(li);
        });
    } catch (error) {
        prependItem('system-events', `<strong>MONITORING</strong> — Erreur de lecture des sessions : ${error.message}`);
    }
}

if (pingButton) {
    pingButton.addEventListener('click', sendPing);
}

if (refreshSessionsButton) {
    refreshSessionsButton.addEventListener('click', loadSessions);
}

connect();
setInterval(loadSessions, 10000);

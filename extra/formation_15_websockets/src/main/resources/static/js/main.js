'use strict';

var sectionPage = document.querySelector('#section-chat');
var formPseudo = document.querySelector('#formPseudo');
var formMessage = document.querySelector('#formMessage');
var messageInput = document.querySelector('#message');
var sectionPseudo = document.querySelector('#section-pseudo');
var zoneMessage = document.querySelector('#zoneMessage');
var baliseConnexion = document.querySelector('.connexion');

var stompClient = null;
var pseudo = null;

var couleurs = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function seConnecter(event) {
    pseudo = document.querySelector('#pseudo').value.trim();

    if(pseudo) {
        sectionPseudo.classList.add('hidden');
        sectionPage.classList.remove('hidden');

        var socket = new SockJS('/websocket');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, souscrireEtSePresenter, onError);
    }
    event.preventDefault();
}


function souscrireEtSePresenter() {
    // Souscrire au topic public pour écouter/envoyer messages
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Se présenter avec son pseudo
    stompClient.send("/app/forum.souscrire",
        {},
        JSON.stringify({expediteur: pseudo, type: 'JOINDRE'})
    )

    baliseConnexion.classList.add('hidden');
}


function onError(error) {
    baliseConnexion.textContent = 'Erreur ! Contacter le gérant du forum. ';
    baliseConnexion.style.color = 'red';
}


function envoyer(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var messageForum = {
            expediteur: pseudo,
            contenu: messageInput.value,
            type: 'DISCUTER'
        };

        stompClient.send("/app/forum.envoyer", {}, JSON.stringify(messageForum));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOINDRE') {
        messageElement.classList.add('event-message');
        message.contenu = message.expediteur + ' a rejoint le chat !';
    } else if (message.type === 'QUITTER') {
        messageElement.classList.add('event-message');
        message.contenu = message.expediteur + ' a quitté le chat !';
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.expediteur[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = couleurParticipant(message.expediteur);

        messageElement.appendChild(avatarElement);

        var pseudoElement = document.createElement('span');
        var pseudoText = document.createTextNode(message.expediteur);
        pseudoElement.appendChild(pseudoText);
        messageElement.appendChild(pseudoElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.contenu);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    zoneMessage.appendChild(messageElement);
    zoneMessage.scrollTop = zoneMessage.scrollHeight;
}


function couleurParticipant(messageEnvoye) {
    var hash = 0;
    for (var i = 0; i < messageEnvoye.length; i++) {
        hash = 31 * hash + messageEnvoye.charCodeAt(i);
    }

    var index = Math.abs(hash % couleurs.length);
    return couleurs[index];
}

formPseudo.addEventListener('submit', seConnecter, true)
formMessage.addEventListener('submit', envoyer, true)
window.addEventListener("beforeunload", function() { 
     var messageForum = {
            expediteur: pseudo,
            contenu: 'Au revoir',
            type: 'QUITTER'
        };
  stompClient.send("/app/forum.envoyer", {}, JSON.stringify(messageForum)); 
  
  });
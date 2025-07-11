package com.formation.springbootwebsocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import com.formation.springbootwebsocket.model.MessageForum;

@Controller
public class ControlleurForum {

	@MessageMapping({"/forum.envoyer", "/forum.souscrire"})
    @SendTo("/topic/public")
    public MessageForum sendMessage(@Payload MessageForum chatMessage) {
        return chatMessage;
    }
}

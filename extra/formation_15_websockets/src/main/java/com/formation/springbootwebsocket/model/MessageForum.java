package com.formation.springbootwebsocket.model;


public class MessageForum {
    private String contenu;
    private String expediteur;
    private MessageType type;

    public enum MessageType {
        DISCUTER, QUITTER, JOINDRE
    }

	public String getContenu() {
		return contenu;
	}

	public void setContenu(String contenu) {
		this.contenu = contenu;
	}

	public String getExpediteur() {
		return expediteur;
	}

	public void setExpediteur(String expediteur) {
		this.expediteur = expediteur;
	}

	public MessageType getType() {
		return type;
	}

	public void setType(MessageType type) {
		this.type = type;
	}


    
}

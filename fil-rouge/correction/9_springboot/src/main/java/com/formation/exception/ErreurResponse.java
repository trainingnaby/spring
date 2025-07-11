package com.formation.exception;

public class ErreurResponse {
	
	private String timestamp;
	private int status;
	private String message;
	private String path;
	
	public ErreurResponse(int status, String message, String path) {
		this.timestamp = System.currentTimeMillis() + "";
		this.status = status;
		this.message = message;
		this.path = path;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	

}

package com.rviewer.skeletons.domain.api;

import lombok.Getter;

@Getter
public class APIClientException extends RuntimeException{
	private String message;
	
	public APIClientException(String message) {
		super(message);
		this.message = message;
	}
}

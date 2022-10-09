package com.rviewer.skeletons.exceptions;

import org.springframework.http.HttpStatus;

public enum APIError {
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "There are attributes with wrong values"),
	LIMIT_ERROR(HttpStatus.FORBIDDEN, "Request rate limit exceeded"),
	INVALID_USER(HttpStatus.BAD_REQUEST, "Invalid user");
	
	private final HttpStatus httpStatus;
	private final String message;
	
	APIError(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}
	
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	
	public String getMessage() {
		return message;
	}
}

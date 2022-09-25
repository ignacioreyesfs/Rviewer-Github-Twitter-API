package com.rviewer.skeletons.domain.api.twitter;

public class TwitterUserNotFoundException extends RuntimeException{
	public TwitterUserNotFoundException(String username) {
		super(String.format("Twitter user not found %s", username));
	}
}

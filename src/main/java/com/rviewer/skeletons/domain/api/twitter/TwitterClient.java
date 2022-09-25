package com.rviewer.skeletons.domain.api.twitter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rviewer.skeletons.domain.api.APIClientException;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Component
@PropertySource("classpath:url.properties")
@Log
public class TwitterClient implements ITwitterClient {
	private final String TWITTER_ERROR = "Error on %s with status code %d";
	private Environment env;
	private WebClient client;
	
	public TwitterClient(Environment env, @Qualifier("twitter") WebClient client) {
		this.env = env;
		this.client = client;
	}

	@Override
	public String getUserIdByUsername(String username) throws TwitterUserNotFoundException {
		TwitterUser user = client.get()
			.uri(env.getProperty("twitter.url.user.byUsername"), username)
			.retrieve()
			.onStatus(HttpStatus::isError, error -> {
				log.severe(String.format(TWITTER_ERROR, "getUserIdByUsername", error.rawStatusCode()));
				return Mono.error(() -> new APIClientException(
						String.format(TWITTER_ERROR, "getUserIdByUsername", error.rawStatusCode())));
				})
			.bodyToMono(TwitterUser.class)
			.block();
		
		if(user == null) {
			throw new APIClientException("Twitter error, user not retrieved");
		}
		
		if(user.isUserDoesNotExists()) {
			throw new TwitterUserNotFoundException(username);
		}
		
		return user.getId();
	}
	
}

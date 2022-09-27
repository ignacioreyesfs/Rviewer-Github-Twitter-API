package com.rviewer.skeletons.domain.api.twitter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
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
			.uri(builder -> builder.path(env.getProperty("twitter.url.user.byUsername"))
					.queryParam("screen_name", username)
					.build())
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
		
		if(user.isHasErrors()) {
			throw new TwitterUserNotFoundException(username);
		}
		
		return user.getId();
	}

	@Override
	public boolean mutualFollowers(String idSource, String idTarget) {
		JsonNode response = client.get()
				.uri(builder -> builder.path(env.getProperty("twitter.url.mutuals"))
						.queryParam("source_id", idSource)
						.queryParam("target_id", idTarget)
						.build())
				.retrieve()
				.onStatus(HttpStatus::isError, error -> {
					log.severe(String.format(TWITTER_ERROR, "mutualFollowers", error.rawStatusCode()));
					return Mono.error(() -> new APIClientException(
							String.format(TWITTER_ERROR, "mutualFollowers", error.rawStatusCode())));
					})
				.bodyToMono(JsonNode.class)
				.block();
		if(response == null || response.has("errors")) {
			log.info("errors");
			return false;
		}
		
		JsonNode source = response.get("relationship").get("source");
		log.info("source: " + source.toString());
				
		return source.get("following").asBoolean() && source.get("followed_by").asBoolean();
	}
}

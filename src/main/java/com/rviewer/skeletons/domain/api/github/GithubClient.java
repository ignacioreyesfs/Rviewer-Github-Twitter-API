package com.rviewer.skeletons.domain.api.github;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.rviewer.skeletons.domain.api.APIClientException;
import com.rviewer.skeletons.domain.api.LimitExceededException;

import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Component
@PropertySource("classpath:url.properties")
@Log
public class GithubClient implements IGithubClient{
	private Environment env;
	private WebClient client;
	
	public GithubClient(Environment env, @Qualifier("github") WebClient client) {
		this.env = env;
		this.client = client;
	}

	@Override
	public boolean userExists(String username) {
		ResponseEntity<String> res = client.get()
			.uri(builder -> builder.path(env.getProperty("github.url.user.byUsername")).build(username))
			.exchangeToMono(response -> response.toEntity(String.class))
			.block();
		
		if(res.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
			log.info("Github limit exceeded");
			throw new LimitExceededException();
		}
		
		log.info(String.format("%s userExists status code: %d", username, res.getStatusCodeValue()));
		
		return res.getStatusCode().equals(HttpStatus.OK);
	}

	@Override
	public boolean userFollows(String sourceUsername, String targetUsername) {
		ResponseEntity<String> res = client.get()
			.uri(builder -> builder.path(env.getProperty("github.url.follows"))
					.build(sourceUsername, targetUsername))
			.exchangeToMono(response -> response.toEntity(String.class))
			.block();
		
		if(res.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
			log.info("Github limit exceeded");
			throw new LimitExceededException();
		}
		
		log.info("userFollows status code: " + res.getStatusCodeValue());
		
		return res.getStatusCode().is2xxSuccessful();
	}

	@Override
	public List<Organization> findOrganizationsByUsername(String username) {
		List<Organization> orgs = new ArrayList<>();
		List<Organization> partialOrgs;
		int page = 1;
		
		do {
			final int tempPage = page;
			partialOrgs = client.get()
					.uri(builder -> builder.path(env.getProperty("github.url.organizations"))
							.queryParam("per_page", 100)
							.queryParam("page", tempPage)
							.build(username))
					.retrieve()
					.onStatus(status -> status.equals(HttpStatus.FORBIDDEN), response -> Mono.error(LimitExceededException::new))
					.onStatus(HttpStatus::isError, response -> Mono.error(new APIClientException("Github error")))
					.bodyToMono(new ParameterizedTypeReference<List<Organization>>() {})
					.block();
			orgs.addAll(partialOrgs);
			page++;
		}while(partialOrgs.size() == 100);
				
		return orgs;
	}

}

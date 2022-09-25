package com.rviewer.skeletons.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@PropertySource("classpath:url.properties")
@PropertySources(value = {@PropertySource("classpath:url.properties"),
		@PropertySource("classpath:secrets.properties")})
public class WebClientConfig {
	@Autowired
	private Environment env;
	
	@Bean(name = "twitter")
	public WebClient twitterWebClient() {
		return WebClient.builder()
				.baseUrl(env.getProperty("twitter.url.base"))
				.defaultHeader(HttpHeaders.AUTHORIZATION, env.getProperty("twitter.auth"))
				.defaultHeader(HttpHeaders.ACCEPT, "application/json")
				.build();
	}
}

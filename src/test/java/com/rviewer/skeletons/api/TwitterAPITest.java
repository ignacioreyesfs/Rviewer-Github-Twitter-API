package com.rviewer.skeletons.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import com.rviewer.skeletons.domain.api.APIClientException;
import com.rviewer.skeletons.domain.api.twitter.ITwitterClient;
import com.rviewer.skeletons.domain.api.twitter.TwitterClient;
import com.rviewer.skeletons.domain.api.twitter.TwitterUserNotFoundException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:url.properties")
public class TwitterAPITest {
	@Autowired
	private Environment env;
	private static MockWebServer mockServer;
	private ITwitterClient twitterClient;
	
	@BeforeAll
	static void setUp() throws IOException {
		mockServer = new MockWebServer();
		mockServer.start();
	}
	
	@AfterAll
	static void tearDown() throws IOException {
		mockServer.shutdown();
	}
	
	@BeforeEach
	public void beforeEach() {
		WebClient client = WebClient.builder()
				.baseUrl(String.format("http://localhost:%d", mockServer.getPort()))
				.defaultHeader(HttpHeaders.ACCEPT, "application/json")
				.build();
		twitterClient = new TwitterClient(env, client);
	}
	
	@Test
	public void getUserIdTest() {
		String response = "{\"data\":{\"id\":\"783214\",\"name\":\"Twitter\",\"username\":\"Twitter\"}}";
		mockServer.enqueue(new MockResponse()
			      .setBody(response)
			      .addHeader("Content-Type", "application/json"));
		String userId = twitterClient.getUserIdByUsername("twitter");
		assertNotNull(userId);
	}
	
	@Test
	public void getUserNotExistsTest() {
		String response = "{\"errors\":[{\"value\":\"nonexist\",\"detail\":\"Could not find user with username: [nonexist].\",\"title\":\"Not Found Error\",\"resource_type\":\"user\",\"parameter\":\"username\",\"resource_id\":\"nonexist\",\"type\":\"https://api.twitter.com/2/problems/resource-not-found\"}]}";
		mockServer.enqueue(new MockResponse()
			      .setBody(response)
			      .addHeader("Content-Type", "application/json"));
		assertThrows(TwitterUserNotFoundException.class, () -> twitterClient.getUserIdByUsername("nonexist"));
	}
	
	@Test
	public void getBadFormatUserTest() {
		mockServer.enqueue(new MockResponse()
				  .setResponseCode(400));
		assertThrows(APIClientException.class, () -> twitterClient.getUserIdByUsername("twitter%"));
	}
}

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
		String response = "{\"id\":783214,\"id_str\":\"783214\",\"name\":\"Twitter\",\"screen_name\":\"Twitter\",\"location\":\"everywhere\",\"profile_location\":null,\"description\":\"What's happening?!\",\"url\":\"https:\\/\\/t.co\\/DAtOo6uuHk\",\"entities\":{\"url\":{\"urls\":[{\"url\":\"https:\\/\\/t.co\\/DAtOo6uuHk\",\"expanded_url\":\"https:\\/\\/about.twitter.com\\/\",\"display_url\":\"about.twitter.com\",\"indices\":[0,23]}]},\"description\":{\"urls\":[]}},\"protected\":false,\"followers_count\":63442333,\"friends_count\":6,\"listed_count\":87549,\"created_at\":\"Tue Feb 20 14:35:54 +0000 2007\",\"favourites_count\":6213,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":true,\"verified\":true,\"statuses_count\":15036,\"lang\":null,\"status\":{\"created_at\":\"Thu Sep 01 12:40:08 +0000 2022\",\"id\":1565318587736285184,\"id_str\":\"1565318587736285184\",\"text\":\"if you see an edited Tweet it's because we're testing the edit button\\n\\nthis is happening and you'll be okay\",\"truncated\":false,\"entities\":{\"hashtags\":[],\"symbols\":[],\"user_mentions\":[],\"urls\":[]},\"source\":\"\\u003ca href=\\\"https:\\/\\/www.sprinklr.com\\\" rel=\\\"nofollow\\\"\\u003eSprinklr\\u003c\\/a\\u003e\",\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"is_quote_status\":false,\"retweet_count\":49008,\"favorite_count\":369907,\"favorited\":false,\"retweeted\":false,\"lang\":\"en\"},\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"ACDED6\",\"profile_background_image_url\":\"http:\\/\\/abs.twimg.com\\/images\\/themes\\/theme18\\/bg.gif\",\"profile_background_image_url_https\":\"https:\\/\\/abs.twimg.com\\/images\\/themes\\/theme18\\/bg.gif\",\"profile_background_tile\":true,\"profile_image_url\":\"http:\\/\\/pbs.twimg.com\\/profile_images\\/1488548719062654976\\/u6qfBBkF_normal.jpg\",\"profile_image_url_https\":\"https:\\/\\/pbs.twimg.com\\/profile_images\\/1488548719062654976\\/u6qfBBkF_normal.jpg\",\"profile_banner_url\":\"https:\\/\\/pbs.twimg.com\\/profile_banners\\/783214\\/1646075315\",\"profile_link_color\":\"1B95E0\",\"profile_sidebar_border_color\":\"FFFFFF\",\"profile_sidebar_fill_color\":\"F6F6F6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"has_extended_profile\":true,\"default_profile\":false,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null,\"translator_type\":\"regular\",\"withheld_in_countries\":[]}";
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

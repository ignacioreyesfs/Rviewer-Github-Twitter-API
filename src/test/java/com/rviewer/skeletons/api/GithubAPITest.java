package com.rviewer.skeletons.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;

import com.rviewer.skeletons.domain.api.github.GithubClient;
import com.rviewer.skeletons.domain.api.github.IGithubClient;
import com.rviewer.skeletons.domain.api.github.Organization;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

@SpringBootTest
@ActiveProfiles("test")
@PropertySource("classpath:url.properties")
public class GithubAPITest {
	private IGithubClient client;
	private static MockWebServer server;
	@Autowired
	private Environment env;
	
	@BeforeAll
	public static void setUp() throws IOException {
		server = new MockWebServer();
		server.start();
	}
	
	@AfterAll
	public static void tearDown() throws IOException {
		server.shutdown();
	}
	
	@BeforeEach
	public void beforeEach() {
		WebClient webClient = WebClient.builder()
				.baseUrl(String.format("http://localhost:%d", server.getPort()))
				.defaultHeader("Content-Type", "application/json")
				.build();
		client = new GithubClient(env, webClient);
	}
	
	@Test
	public void userExistsTest() {
		String response = "{\"login\":\"test\",\"id\":383316,\"node_id\":\"MDQ6VXNlcjM4MzMxNg==\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/383316?v=4\",\"gravatar_id\":\"\",\"url\":\"https://api.github.com/users/test\",\"html_url\":\"https://github.com/test\",\"followers_url\":\"https://api.github.com/users/test/followers\",\"following_url\":\"https://api.github.com/users/test/following{/other_user}\",\"gists_url\":\"https://api.github.com/users/test/gists{/gist_id}\",\"starred_url\":\"https://api.github.com/users/test/starred{/owner}{/repo}\",\"subscriptions_url\":\"https://api.github.com/users/test/subscriptions\",\"organizations_url\":\"https://api.github.com/users/test/orgs\",\"repos_url\":\"https://api.github.com/users/test/repos\",\"events_url\":\"https://api.github.com/users/test/events{/privacy}\",\"received_events_url\":\"https://api.github.com/users/test/received_events\",\"type\":\"User\",\"site_admin\":false,\"name\":null,\"company\":null,\"blog\":\"\",\"location\":null,\"email\":null,\"hireable\":null,\"bio\":null,\"twitter_username\":null,\"public_repos\":5,\"public_gists\":0,\"followers\":45,\"following\":0,\"created_at\":\"2010-09-01T10:39:12Z\",\"updated_at\":\"2020-04-24T20:58:44Z\"}";
		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(response)
				.addHeader("Content-Type", "application/json"));
		assertTrue(client.userExists("test"));
		
		response = "{\"message\":\"Not Found\",\"documentation_url\":\"https://docs.github.com/rest/reference/users#get-a-user\"}";
		server.enqueue(new MockResponse()
				.setResponseCode(404)
				.setBody(response)
				.addHeader("Content-Type", "application/json"));
		assertFalse(client.userExists("test22"));
	}
	
	@Test
	public void userFollowTest() {
		server.enqueue(new MockResponse().setResponseCode(204));
		assertTrue(client.userFollows("test", "test2"));
		server.enqueue(new MockResponse().setResponseCode(404));
		assertFalse(client.userFollows("test2", "test"));
	}
	
	@Test
	public void findOrganizationsTest() {
		String response = "[{\"login\":\"perltaiwan\",\"id\":1469038,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjE0NjkwMzg=\",\"url\":\"https://api.github.com/orgs/perltaiwan\",\"repos_url\":\"https://api.github.com/orgs/perltaiwan/repos\",\"events_url\":\"https://api.github.com/orgs/perltaiwan/events\",\"hooks_url\":\"https://api.github.com/orgs/perltaiwan/hooks\",\"issues_url\":\"https://api.github.com/orgs/perltaiwan/issues\",\"members_url\":\"https://api.github.com/orgs/perltaiwan/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/perltaiwan/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/1469038?v=4\",\"description\":\"\"},{\"login\":\"golangtw\",\"id\":2357470,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjIzNTc0NzA=\",\"url\":\"https://api.github.com/orgs/golangtw\",\"repos_url\":\"https://api.github.com/orgs/golangtw/repos\",\"events_url\":\"https://api.github.com/orgs/golangtw/events\",\"hooks_url\":\"https://api.github.com/orgs/golangtw/hooks\",\"issues_url\":\"https://api.github.com/orgs/golangtw/issues\",\"members_url\":\"https://api.github.com/orgs/golangtw/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/golangtw/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/2357470?v=4\",\"description\":null},{\"login\":\"g0v\",\"id\":2668086,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjI2NjgwODY=\",\"url\":\"https://api.github.com/orgs/g0v\",\"repos_url\":\"https://api.github.com/orgs/g0v/repos\",\"events_url\":\"https://api.github.com/orgs/g0v/events\",\"hooks_url\":\"https://api.github.com/orgs/g0v/hooks\",\"issues_url\":\"https://api.github.com/orgs/g0v/issues\",\"members_url\":\"https://api.github.com/orgs/g0v/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/g0v/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/2668086?v=4\",\"description\":\"\"},{\"login\":\"phpbrew\",\"id\":7889581,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjc4ODk1ODE=\",\"url\":\"https://api.github.com/orgs/phpbrew\",\"repos_url\":\"https://api.github.com/orgs/phpbrew/repos\",\"events_url\":\"https://api.github.com/orgs/phpbrew/events\",\"hooks_url\":\"https://api.github.com/orgs/phpbrew/hooks\",\"issues_url\":\"https://api.github.com/orgs/phpbrew/issues\",\"members_url\":\"https://api.github.com/orgs/phpbrew/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/phpbrew/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/7889581?v=4\",\"description\":\"The PHPBrew Headquarters\"},{\"login\":\"titanlink9\",\"id\":13128827,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjEzMTI4ODI3\",\"url\":\"https://api.github.com/orgs/titanlink9\",\"repos_url\":\"https://api.github.com/orgs/titanlink9/repos\",\"events_url\":\"https://api.github.com/orgs/titanlink9/events\",\"hooks_url\":\"https://api.github.com/orgs/titanlink9/hooks\",\"issues_url\":\"https://api.github.com/orgs/titanlink9/issues\",\"members_url\":\"https://api.github.com/orgs/titanlink9/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/titanlink9/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/13128827?v=4\",\"description\":\"\"},{\"login\":\"phpsgi\",\"id\":13925453,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjEzOTI1NDUz\",\"url\":\"https://api.github.com/orgs/phpsgi\",\"repos_url\":\"https://api.github.com/orgs/phpsgi/repos\",\"events_url\":\"https://api.github.com/orgs/phpsgi/events\",\"hooks_url\":\"https://api.github.com/orgs/phpsgi/hooks\",\"issues_url\":\"https://api.github.com/orgs/phpsgi/issues\",\"members_url\":\"https://api.github.com/orgs/phpsgi/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/phpsgi/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/13925453?v=4\",\"description\":null},{\"login\":\"maghead\",\"id\":25189407,\"node_id\":\"MDEyOk9yZ2FuaXphdGlvbjI1MTg5NDA3\",\"url\":\"https://api.github.com/orgs/maghead\",\"repos_url\":\"https://api.github.com/orgs/maghead/repos\",\"events_url\":\"https://api.github.com/orgs/maghead/events\",\"hooks_url\":\"https://api.github.com/orgs/maghead/hooks\",\"issues_url\":\"https://api.github.com/orgs/maghead/issues\",\"members_url\":\"https://api.github.com/orgs/maghead/members{/member}\",\"public_members_url\":\"https://api.github.com/orgs/maghead/public_members{/member}\",\"avatar_url\":\"https://avatars.githubusercontent.com/u/25189407?v=4\",\"description\":\"The Fast ORM for PHP!\"}]";
		
		server.enqueue(new MockResponse()
				.setResponseCode(200)
				.setBody(response)
				.setHeader("Content-Type", "application/json"));
		
		List<Organization> orgs = client.findOrganizationsByUsername("test");
		assertEquals(7, orgs.size());
		assertTrue(orgs.stream().anyMatch(org -> org.getName().equals("perltaiwan")));
	}
}

package com.rviewer.skeletons.domain.api.github;

import java.util.List;

public interface IGithubClient {
	public boolean userExists(String username);
	public boolean userFollows(String sourceUsername, String targetUsername);
	public List<Organization> findOrganizationsByUsername(String username);
}

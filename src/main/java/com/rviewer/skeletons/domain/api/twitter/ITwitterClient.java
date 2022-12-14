package com.rviewer.skeletons.domain.api.twitter;

public interface ITwitterClient {
	public String getUserIdByUsername(String username) throws TwitterUserNotFoundException;
	public boolean mutualFollowers(String idSource, String idTarget);
}

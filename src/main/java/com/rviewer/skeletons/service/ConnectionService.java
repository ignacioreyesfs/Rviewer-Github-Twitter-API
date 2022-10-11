package com.rviewer.skeletons.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rviewer.skeletons.dao.ConnectionRepository;
import com.rviewer.skeletons.domain.UsersConnection;
import com.rviewer.skeletons.domain.api.github.IGithubClient;
import com.rviewer.skeletons.domain.api.github.Organization;
import com.rviewer.skeletons.domain.api.twitter.ITwitterClient;
import com.rviewer.skeletons.domain.api.twitter.TwitterUserNotFoundException;
import com.rviewer.skeletons.mapper.UsersConnectionMapper;
import com.rviewer.skeletons.service.dto.RegisterDTO;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@AllArgsConstructor
@Log
@Service
public class ConnectionService {
	private IGithubClient github;
	private ITwitterClient twitter;
	private ConnectionRepository connectionRepo;
	private UsersConnectionMapper mapper;
	
	public boolean githubUserExists(String username) {
		return github.userExists(username);
	}
	
	public boolean twitterUserExists(String username) {
		try {
			twitter.getUserIdByUsername(username);
		}catch(TwitterUserNotFoundException e){
			return false;
		}
		
		return true;
	}
	
	public UsersConnection getConnectionBetween(String username1, String username2) {
		UsersConnection userConnection = new UsersConnection(username1, username2);
		String twitterId1;
		String twitterId2;
		
		userConnection.setRegisteredAt(LocalDateTime.now());
		
		try {
			twitterId1 = twitter.getUserIdByUsername(username1);
			twitterId2 = twitter.getUserIdByUsername(username2);
		}catch (TwitterUserNotFoundException e) {
			userConnection.setConnected(false);
			return userConnection;
		}
		
		boolean connected = twitter.mutualFollowers(twitterId1, twitterId2)
				&& github.userFollows(username1, username2) && github.userFollows(username2, username1);
		
		userConnection.setConnected(connected);
		if(connected) {
			userConnection.setOrganizations(getOrganizationsInCommmon(username1, username2));
		}
		
		return userConnection;
	}
	
	private List<Organization> getOrganizationsInCommmon(String username1, String username2){
		List<Organization> orgs1 = github.findOrganizationsByUsername(username1);
		List<Organization> orgs2 = github.findOrganizationsByUsername(username2);
		
		return orgs1.stream().filter(orgs2::contains).toList();
	}

	public void save(UsersConnection usersConnection) {
		log.info("saved");
		connectionRepo.save(usersConnection);
	}

	public List<RegisterDTO> getRegistersOf(String dev1, String dev2) {
		return connectionRepo.findByUsername1AndUsername2OrderByRegisteredAtDesc(dev1, dev2).stream()
				.map(mapper::toRegisterDTO)
				.toList();
	}
}

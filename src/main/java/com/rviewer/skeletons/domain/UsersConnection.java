package com.rviewer.skeletons.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.rviewer.skeletons.domain.api.github.Organization;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UsersConnection {
	private String username1;
	private String username2;
	private boolean connected;
	private List<Organization> organizations;
	private LocalDateTime registeredAt;
	
	public UsersConnection(String username1, String username2) {
		this.username1 = username1;
		this.username2 = username2;
	}
}

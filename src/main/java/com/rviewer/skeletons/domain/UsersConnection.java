package com.rviewer.skeletons.domain;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.rviewer.skeletons.domain.api.github.Organization;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name="users_connection")
public class UsersConnection {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username1;
	private String username2;
	private boolean connected;
	@ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
	@JoinTable(name="connection_organization", joinColumns = @JoinColumn(name = "connection_id"),
			inverseJoinColumns = @JoinColumn(name = "organization_id"))
	private List<Organization> organizations;
	private LocalDateTime registeredAt;
	
	public UsersConnection(String username1, String username2) {
		this.username1 = username1;
		this.username2 = username2;
	}
}

package com.rviewer.skeletons.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rviewer.skeletons.domain.UsersConnection;

public interface ConnectionRepository extends JpaRepository<UsersConnection, Long> {

	public List<UsersConnection> findByUsername1AndUsername2OrderByRegisteredAtDesc(String dev1, String dev2);

}

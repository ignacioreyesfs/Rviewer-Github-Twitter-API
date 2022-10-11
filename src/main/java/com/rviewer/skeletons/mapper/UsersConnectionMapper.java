package com.rviewer.skeletons.mapper;

import org.springframework.stereotype.Component;

import com.rviewer.skeletons.controller.dto.RealtimeConnectionDTO;
import com.rviewer.skeletons.domain.UsersConnection;
import com.rviewer.skeletons.domain.api.github.Organization;
import com.rviewer.skeletons.service.dto.RegisterDTO;

@Component
public class UsersConnectionMapper {
	public RealtimeConnectionDTO toRealtimeConnectionDTO(UsersConnection connection) {
		RealtimeConnectionDTO realtime = new RealtimeConnectionDTO();
		realtime.setConnected(connection.isConnected());
		if(connection.getOrganizations() != null && !connection.getOrganizations().isEmpty()) {
			realtime.setOrganizations(connection.getOrganizations()
					.stream().map(Organization::getName).toList());
		}
		
		return realtime;
	}
	
	public RegisterDTO toRegisterDTO(UsersConnection connection) {
		RegisterDTO register = new RegisterDTO();
		register.setConnected(connection.isConnected());
		register.setRegisterAt(connection.getRegisteredAt());
		if(connection.getOrganizations() != null && !connection.getOrganizations().isEmpty()) {
			register.setOrganizations(connection.getOrganizations()
					.stream().map(Organization::getName).toList());
		}
		
		return register;
	}
}

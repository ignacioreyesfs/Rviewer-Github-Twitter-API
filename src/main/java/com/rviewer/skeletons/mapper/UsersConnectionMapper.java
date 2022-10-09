package com.rviewer.skeletons.mapper;

import org.springframework.stereotype.Component;

import com.rviewer.skeletons.controller.dto.RealtimeConnectionDTO;
import com.rviewer.skeletons.domain.UsersConnection;
import com.rviewer.skeletons.domain.api.github.Organization;

@Component
public class UsersConnectionMapper {
	public RealtimeConnectionDTO toRealtimeConnectionDTO(UsersConnection connection) {
		RealtimeConnectionDTO realtime = new RealtimeConnectionDTO();
		realtime.setConnected(connection.isConnected());
		if(connection.isConnected() && !connection.getOrganizations().isEmpty()) {
			realtime.setOrganizations(connection.getOrganizations()
					.stream().map(Organization::getName).toList());
		}
		
		return realtime;
	}
}

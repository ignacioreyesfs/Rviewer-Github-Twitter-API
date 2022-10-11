package com.rviewer.skeletons.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.rviewer.skeletons.controller.dto.RealtimeConnectionDTO;
import com.rviewer.skeletons.domain.UsersConnection;
import com.rviewer.skeletons.exceptions.APIError;
import com.rviewer.skeletons.mapper.UsersConnectionMapper;
import com.rviewer.skeletons.service.ConnectionService;
import com.rviewer.skeletons.service.dto.RegisterDTO;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@AllArgsConstructor
@RestController
@Log
public class ConnectionController {
	private final String INVALID_GITHUB_USER = "%s is not a valid user in github";
	private final String INVALID_TWITTER_USER = "%s is not a valid user in twitter";
	private ConnectionService connectionService;
	private UsersConnectionMapper mapper;
	
	@GetMapping("/connected/realtime/{dev1}/{dev2}")
	public ResponseEntity<RealtimeConnectionDTO> getRealtimeConnection(@PathVariable String dev1,
			@PathVariable String dev2) {
		RealtimeConnectionDTO realtime;
		HttpStatus status;
		UsersConnection usersConnection;
		List<String> nonExistents = getNonExistentUsers(dev1, dev2);
		
		if(!nonExistents.isEmpty()) {
			throw new InvalidUsersException(APIError.INVALID_USER, nonExistents);
		}
		
		usersConnection = connectionService.getConnectionBetween(dev1, dev2);
		realtime = mapper
				.toRealtimeConnectionDTO(usersConnection);
		status = realtime.isConnected()? HttpStatus.OK: HttpStatus.NOT_FOUND;
		
		connectionService.save(usersConnection);
		
		return ResponseEntity.status(status).body(realtime);
	}
	
	@GetMapping("/connected/register/{dev1}/{dev2}")
	public ResponseEntity<List<RegisterDTO>> getRegisterConnections(@PathVariable String dev1,
			@PathVariable String dev2){
		List<RegisterDTO> registers = connectionService.getRegistersOf(dev1, dev2);
		log.info("registers: " + registers.size());
		if(registers.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(registers);
	}
	
	private List<String> getNonExistentUsers(String user1, String user2){
		List<String> errors = new ArrayList<>();
		if(!connectionService.twitterUserExists(user1)) {
			errors.add(String.format(INVALID_TWITTER_USER, user1));
		}
		if(!connectionService.githubUserExists(user1)) {
			errors.add(String.format(INVALID_GITHUB_USER, user1));
		}
		if(!connectionService.twitterUserExists(user2)) {
			errors.add(String.format(INVALID_TWITTER_USER, user2));
		}
		if(!connectionService.githubUserExists(user2)) {
			errors.add(String.format(INVALID_GITHUB_USER, user2));
		}
		
		return errors;
	}
}

package com.rviewer.skeletons.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rviewer.skeletons.domain.api.github.IGithubClient;
import com.rviewer.skeletons.domain.api.github.Organization;
import com.rviewer.skeletons.domain.api.twitter.ITwitterClient;
import com.rviewer.skeletons.service.dto.RegisterDTO;

import lombok.extern.java.Log;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@Log
@ActiveProfiles("test")
public class ConnectionControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objMapper;
	@MockBean
	private IGithubClient github;
	@MockBean
	private ITwitterClient twitter;
	
	@Test
	public void connectionRegisterTest() throws Exception {
		when(github.userExists(any())).thenReturn(true);
		when(github.userFollows(any(), any())).thenReturn(false);
		when(twitter.getUserIdByUsername(any())).thenReturn("1");
		when(twitter.mutualFollowers(any(), any())).thenReturn(true);
		
		makeRegisterRequest("test1", "test2", 404);
		
		makeRealtimeRequest("test1", "test2", 404);
		
		when(github.userFollows(any(), any())).thenReturn(true);
		when(github.findOrganizationsByUsername(any())).thenReturn(Arrays.asList(new Organization("1", "testOrg")));
		makeRealtimeRequest("test1", "test2", 200);
		
		MvcResult result = makeRegisterRequest("test1", "test2", 200);
		List<RegisterDTO> registers = objMapper.readValue(result.getResponse().getContentAsByteArray(),
				new TypeReference<List<RegisterDTO>>(){});
		assertEquals(2, registers.size());
		log.info(registers.toString());
	}
	
	private void makeRealtimeRequest(String dev1, String dev2, int expectedStatus) throws Exception {
		mockMvc.perform(get("/connected/realtime/{dev1}/{dev2}", dev1, dev2)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is(expectedStatus));
	}
	
	private MvcResult makeRegisterRequest(String dev1, String dev2, int expectedStatus) throws Exception{
		return mockMvc.perform(get("/connected/register/{dev1}/{dev2}", dev1, dev2)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().is(expectedStatus))
			.andReturn();
	}
}

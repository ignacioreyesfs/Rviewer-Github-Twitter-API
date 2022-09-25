package com.rviewer.skeletons.domain.api.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TwitterUser {
	private String id;
	private String name;
	private String username;
	private boolean userDoesNotExists;
	
	@JsonProperty("data")
	private void unpackData(JsonNode data) {
		this.id = data.get("id").textValue();
		this.name = data.get("name").textValue();
		this.username = data.get("username").textValue();
	}
	
	@JsonProperty("errors")
	private void unpackErrors(JsonNode errors) {
		this.userDoesNotExists = true;
	}
}

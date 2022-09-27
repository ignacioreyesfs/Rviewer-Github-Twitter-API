package com.rviewer.skeletons.domain.api.twitter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class TwitterUser {
	@JsonProperty("id_str")
	private String id;
	@JsonProperty("name")
	private String name;
	@JsonProperty("screen_name")
	private String username;
	private boolean hasErrors;
	
	@JsonProperty("errors")
	private void unpackErrors(JsonNode errors) {
		this.hasErrors = true;
	}
}

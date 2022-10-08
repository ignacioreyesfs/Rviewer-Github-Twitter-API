package com.rviewer.skeletons.exceptions;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorDTO {
	private String description;
	private List<String> reasons;
}

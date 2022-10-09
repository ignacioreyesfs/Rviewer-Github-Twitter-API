package com.rviewer.skeletons.controller;

import java.util.List;

import com.rviewer.skeletons.exceptions.APIError;
import com.rviewer.skeletons.exceptions.AppException;

public class InvalidUsersException extends AppException{
	public InvalidUsersException(APIError error, List<String> reasons) {
		super(error.getHttpStatus(), error.getMessage(), reasons);
	}
}

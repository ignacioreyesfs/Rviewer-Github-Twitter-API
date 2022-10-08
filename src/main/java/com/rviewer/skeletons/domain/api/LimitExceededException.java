package com.rviewer.skeletons.domain.api;

import com.rviewer.skeletons.exceptions.APIError;
import com.rviewer.skeletons.exceptions.AppException;

public class LimitExceededException extends AppException{
	public LimitExceededException() {
		super(APIError.LIMIT_ERROR.getHttpStatus(), APIError.LIMIT_ERROR.getMessage(), null);
	}
}

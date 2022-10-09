package com.rviewer.skeletons.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.rviewer.skeletons.exceptions.AppException;
import com.rviewer.skeletons.exceptions.ErrorDTO;

@RestControllerAdvice
public class ExceptionController {
	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorDTO> handleAppException(AppException ex){
		return ResponseEntity.status(ex.getStatus()).body(new ErrorDTO(ex.getDescription(), ex.getReasons()));
	}
}

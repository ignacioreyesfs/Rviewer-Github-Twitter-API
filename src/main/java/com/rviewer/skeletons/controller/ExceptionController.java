package com.rviewer.skeletons.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.rviewer.skeletons.exceptions.AppException;
import com.rviewer.skeletons.exceptions.ErrorDTO;

@RestControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler{
	@ExceptionHandler(AppException.class)
	public ResponseEntity<ErrorDTO> handleAppException(AppException ex){
		return ResponseEntity.status(ex.getStatus()).body(new ErrorDTO(ex.getDescription(), ex.getReasons()));
	}
}

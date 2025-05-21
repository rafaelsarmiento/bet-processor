package com.sportradar.bet_processor.controller;

import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BetProcessorControllerAdvice {
	
	private final Logger log = LoggerFactory.getLogger(BetProcessorControllerAdvice.class);

	@ExceptionHandler(IllegalArgumentException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public String illegalArgumentExceptionHandler(IllegalArgumentException exception) {
		log.info("Bad request: {}", exception.getMessage());
		
		return exception.getMessage();
	}
	
	@ExceptionHandler(RejectedExecutionException.class)
	@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
	public void rejectedExecutionExceptionHandler(RejectedExecutionException exception) {
		log.warn("The system is very busy at the moment");
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public void genericExceptionHandler(Exception exception) {
		log.error("Unexpected error", exception);
	}
	
}

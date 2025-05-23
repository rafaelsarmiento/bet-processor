package com.sportradar.bet_processor.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.time.Duration;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import com.sportradar.bet_processor.service.BetProcessorAccumService;

@WebMvcTest
public class BetProcessorControllerTest {
	
	private static final String BETS_URI = "/bet-processor/api/v1/bets";
	private static final String SHUTDOWN_URI = "/bet-processor/api/v1/shutdown";
	private static final String BODY = "{}";

	@Autowired
	private MockMvcTester mvc;
	
	@Autowired
	private BetProcessorController betProcessorController;
	
	@MockitoBean
	private BetProcessorAccumService betProcessorAccumService;
	
	@MockitoBean
	private ApplicationContext applicationContext;
	
	@Test
	public void addBet_wrongBet_return400() {
	
		doThrow(new IllegalArgumentException()).when(betProcessorAccumService).addBet(Mockito.any());
		
		assertThat(mvc.post()
				.uri(BETS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(BODY)
				.exchange())
			.hasStatus(HttpStatus.BAD_REQUEST);
	}
	
	@Test
	public void addBet_busyServer_return503() {
	
		doThrow(new RejectedExecutionException()).when(betProcessorAccumService).addBet(Mockito.any());
		
		assertThat(mvc.post()
				.uri(BETS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(BODY)
				.exchange())
			.hasStatus(HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	@Test
	public void addBet_genericError_return500() {
	
		doThrow(new NullPointerException()).when(betProcessorAccumService).addBet(Mockito.any());
		
		assertThat(mvc.post()
				.uri(BETS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(BODY)
				.exchange())
			.hasStatus(HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@Test
	public void addBet_rightBet_return200() {
	
		doNothing().when(betProcessorAccumService).addBet(Mockito.any());
		
		assertThat(mvc.post()
				.uri(BETS_URI)
				.contentType(MediaType.APPLICATION_JSON)
				.content(BODY)
				.exchange())
			.hasStatus(HttpStatus.OK);
	}
	
	@Test
	public void shutdown_allRight_return200() throws InterruptedException {
	
		doNothing().when(betProcessorAccumService).shutdown();
		
		betProcessorController.setApplicationContext(applicationContext);
		assertThat(mvc.post()
				.uri(SHUTDOWN_URI)
				.exchange())
			.hasStatus(HttpStatus.OK);
		
		Thread.sleep(Duration.ofSeconds(5));
	}

}

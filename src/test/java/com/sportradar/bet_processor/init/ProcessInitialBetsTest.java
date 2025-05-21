package com.sportradar.bet_processor.init;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetProcessorAccumService;

public class ProcessInitialBetsTest {

	private final Resource resource = mock(Resource.class);
	private final ObjectMapper objectMapper = mock(ObjectMapper.class);
	private final BetProcessorAccumService betProcessorAccumService = mock(BetProcessorAccumService.class);
	
	@Test
	public void run() throws Exception {		
		File file = new ClassPathResource("initial-bets.json").getFile();
		when(resource.getFile()).thenReturn(file);
		Bet bet = new Bet(0, 0, 0, null, null, null, null, null);
		when(objectMapper.readValue(file, Bet[].class)).thenReturn(new Bet[]{bet});
		
		ProcessInitialBets processInitialBets = new ProcessInitialBets(resource, objectMapper, betProcessorAccumService);
		processInitialBets.run();
		
		verify(betProcessorAccumService, times(1)).addBet(bet);
	}
	
	@Test
	public void run_validationError_goNextOne() throws Exception {		
		File file = new ClassPathResource("initial-bets.json").getFile();
		when(resource.getFile()).thenReturn(file);
		Bet bet = new Bet(1, 0, 0, null, null, null, null, null);
		Bet bet2 = new Bet(2, 0, 0, null, null, null, null, null);
		when(objectMapper.readValue(file, Bet[].class)).thenReturn(new Bet[]{bet, bet2});
		doThrow(new IllegalArgumentException()).when(betProcessorAccumService).addBet(bet);
		
		ProcessInitialBets processInitialBets = new ProcessInitialBets(resource, objectMapper, betProcessorAccumService);
		processInitialBets.run();
		
		verify(betProcessorAccumService, times(1)).addBet(bet);
		verify(betProcessorAccumService, times(1)).addBet(bet2);
	}
	
	@Test
	public void run_busyError_goNextOne() throws Exception {		
		File file = new ClassPathResource("initial-bets.json").getFile();
		when(resource.getFile()).thenReturn(file);
		Bet bet = new Bet(1, 0, 0, null, null, null, null, null);
		Bet bet2 = new Bet(2, 0, 0, null, null, null, null, null);
		when(objectMapper.readValue(file, Bet[].class)).thenReturn(new Bet[]{bet, bet2});
		doThrow(new RejectedExecutionException()).when(betProcessorAccumService).addBet(bet);
		
		ProcessInitialBets processInitialBets = new ProcessInitialBets(resource, objectMapper, betProcessorAccumService);
		processInitialBets.run();
		
		verify(betProcessorAccumService, times(1)).addBet(bet);
		verify(betProcessorAccumService, times(1)).addBet(bet2);
	}
	
}

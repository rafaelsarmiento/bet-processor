package com.sportradar.bet_processor.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.domain.BetStatus;
import com.sportradar.bet_processor.service.BetProcessorAccumService;
import com.sportradar.bet_processor.service.BetProcessorService;
import com.sportradar.bet_processor.service.BetResultService;

public class MultipleWorkersBetProcessorAccumServiceImplTest {

	private static final String CLIENT = "testClient";
	private static final BetStatus STATUS = BetStatus.OPEN;
	
	private BetProcessorService betProcessorService = mock(BetProcessorService.class);
	private BetResultService betResultService = mock(BetResultService.class);
	
	@Test
	public void addBet_nullBet_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(null))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_nullClient_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(null, STATUS)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_emptyClient_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(" ", STATUS)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_nullStatus_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(CLIENT, null)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_amountEqualZero_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(0, 0.3)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_amountLowerThanZero_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(-0.0000001, 0.3)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_oddsEqualZero_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(10, 0)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_oddsLowerThanZero_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(10, -0.001)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_oddsEqualOne_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(10, 1)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_oddsGreaterThanOne_throwValidationException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
		
		assertThatThrownBy(() -> betProcessor.addBet(createBet(10, 1.00001)))
			.isInstanceOf(IllegalArgumentException.class);
		verify(betProcessorService, never()).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_busyProcessor_throwsRejectedExecutionException() {
		Bet bet = createBet();
	 	when(betProcessorService.processBet(bet)).thenThrow(new RejectedExecutionException());
	 	
	 	BetProcessorAccumService betProcessor = createBetProcessorAccumService();
	 	
	 	assertThatThrownBy(() -> betProcessor.addBet(bet))
	 		.isInstanceOf(RejectedExecutionException.class);
		verify(betProcessorService, times(1)).processBet(Mockito.any());
		verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
		verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_exceptionally_processExceptionResult() {
		Bet bet = createBet();
		Throwable exception = new Exception();
	 	when(betProcessorService.processBet(bet)).thenReturn(CompletableFuture.failedFuture(exception));
	 	
	 	BetProcessorAccumService betProcessor = createBetProcessorAccumService();
	 	betProcessor.addBet(bet);

		verify(betProcessorService, times(1)).processBet(Mockito.any());
	 	verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
	 	verify(betResultService, times(1)).processException(bet, exception);
	}
	
	@Test
	public void addBet_successButNoResult_processNoBetResult() {
		Bet bet = createBet();
	 	when(betProcessorService.processBet(bet)).thenReturn(CompletableFuture.completedFuture(Optional.empty()));
	 	
	 	BetProcessorAccumService betProcessor = createBetProcessorAccumService();
	 	betProcessor.addBet(bet);

		verify(betProcessorService, times(1)).processBet(Mockito.any());
	 	verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
	 	verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_successWithResult_processBetResult() {
		Bet bet = createBet();
		BigDecimal result = BigDecimal.TEN;
	 	when(betProcessorService.processBet(bet)).thenReturn(CompletableFuture.completedFuture(Optional.of(result)));
	 	
	 	BetProcessorAccumService betProcessor = createBetProcessorAccumService();
	 	betProcessor.addBet(bet);

		verify(betProcessorService, times(1)).processBet(Mockito.any());
	 	verify(betResultService, times(1)).processBetResult(bet, result);
	 	verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}
	
	@Test
	public void addBet_duringShutdown_throwsRejectedExecutionException() {
		Bet bet = createBet();
		
		var betProcessor = new MultipleWorkersBetProcessorAccumServiceImpl(betProcessorService, betResultService);
		betProcessor.setApplicationContext(mock(ApplicationContext.class));
	 	betProcessor.shutdown();
	 	
	 	assertThatThrownBy(() -> betProcessor.addBet(bet))
 			.isInstanceOf(RejectedExecutionException.class);
	 	verify(betProcessorService, never()).processBet(Mockito.any());
	 	verify(betResultService, never()).processBetResult(Mockito.any(), Mockito.any());
	 	verify(betResultService, never()).processException(Mockito.any(), Mockito.any());
	}	

	
	@Test
	public void shutdown_whenAppIsStartingUp_throwsIllegalArgumentException() {
		
		BetProcessorAccumService betProcessor = createBetProcessorAccumService();
	 		 	
	 	assertThatThrownBy(() -> betProcessor.shutdown())
 			.isInstanceOf(IllegalArgumentException.class);
	}
	
	
	private BetProcessorAccumService createBetProcessorAccumService() {
		return new MultipleWorkersBetProcessorAccumServiceImpl(betProcessorService, betResultService);
	}
	
	private static Bet createBet() {
		return createBet(CLIENT, STATUS);
	}
	
	private static Bet createBet(double amount, double odds) {
		return new Bet(123, amount, odds, CLIENT, null, null, null, STATUS);
	}
	
	private static Bet createBet(String client, BetStatus status) {
		return new Bet(123, 12.45, 0.3, client, null, null, null, status);
	}
	
}

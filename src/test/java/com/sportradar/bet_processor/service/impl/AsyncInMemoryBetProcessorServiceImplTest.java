package com.sportradar.bet_processor.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.domain.BetStatus;
import com.sportradar.bet_processor.service.BetProcessorService;

public class AsyncInMemoryBetProcessorServiceImplTest {
	
	@Test
	public void processBet_firstIsWinner_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.WINNER));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_firstIsWinner2_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN))
				.thenCompose(dummy -> betProcessor.processBet(createBet(id + 1, BetStatus.WINNER)));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_firstIsLoser_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.LOSER));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_firstIsVoid_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.VOID));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_firstIsOpen_returnEmpty() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN));
		
		Assertions.assertTrue(betResult.join().isEmpty());
	}
	
	@Test
	public void processBet_openAndAlreadyOpen_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.OPEN)));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_openAndAlreadyClose_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.VOID))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.OPEN)));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_closeAndAlreadyClose_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.VOID))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.WINNER)));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_sameStatus_completeExceptionally() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.VOID))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.VOID)));
		
		assertThatThrownBy(betResult::join)
			.cause()
			.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void processBet_openAndVoid_returnZero() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.VOID)));
		
		Optional<BigDecimal> result = betResult.join();
		Assertions.assertFalse(betResult.join().isEmpty());
		Assertions.assertEquals(BigDecimal.ZERO, result.get());
	}
	
	@Test
	public void processBet_openAndWinner_returnPositiveValue() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.WINNER, 10, 0.3)));
		
		Optional<BigDecimal> result = betResult.join();
		Assertions.assertFalse(betResult.join().isEmpty());
		Assertions.assertTrue(BigDecimal.valueOf(7).compareTo(result.get()) == 0);
	}
	
	@Test
	public void processBet_openAndLoser_returnNegativeValue() {
		
		int id = 123;
		BetProcessorService betProcessor = createBetProcessorService();
		var betResult = betProcessor.processBet(createBet(id, BetStatus.OPEN))
			.thenCompose(dummy -> betProcessor.processBet(createBet(id, BetStatus.LOSER, 13.72, 0.3)));
		
		Optional<BigDecimal> result = betResult.join();
		Assertions.assertFalse(betResult.join().isEmpty());
		Assertions.assertTrue(BigDecimal.valueOf(-13.72).compareTo(result.get()) == 0);
	}
	
	
	private BetProcessorService createBetProcessorService() {
		return new AsyncInMemoryBetProcessorServiceImpl(Duration.ofMillis(10));
	}
	
	private static Bet createBet(int id, BetStatus status) {
		return createBet(id, status, 123.45, 0.4);
	}
	
	private static Bet createBet(int id, BetStatus status, double amount, double odds) {
		return new Bet(id, amount, odds, "clientTest", null, null, null, status);
	}
}

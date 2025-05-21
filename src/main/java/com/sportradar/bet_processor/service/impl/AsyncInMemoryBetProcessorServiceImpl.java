package com.sportradar.bet_processor.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.domain.BetStatus;
import com.sportradar.bet_processor.service.BetProcessorService;

/**
 * Asynchronous implementation of a worker processing a bet.
 * The status check will be based in a in-memory registry of previous status.
 * 
 */
@Service
public class AsyncInMemoryBetProcessorServiceImpl implements BetProcessorService {

	private final Logger log = LoggerFactory.getLogger(AsyncInMemoryBetProcessorServiceImpl.class);
	
	private final Duration sleepingDuration; 
	private final Map<Integer, RegisteredBetStatus> betStatusRegister;
	
	public AsyncInMemoryBetProcessorServiceImpl(@Value("${bet-processor.processor.sleeping-time}") Duration sleepingDuration) {
		this.sleepingDuration = sleepingDuration;
		betStatusRegister = new ConcurrentHashMap<>();
	}
	
	@Async
	@Override
	public CompletableFuture<Optional<BigDecimal>> processBet(Bet bet) {
		sleep();		
		var exceptionOp = checkUpdateStatus(bet);
		if (exceptionOp.isEmpty()) {
			return CompletableFuture.completedFuture(calculateResult(bet)); 
		} else {
			return CompletableFuture.failedFuture(exceptionOp.get());
		}	
	}

	private void sleep() {
		try {
			Thread.sleep(sleepingDuration);
		} catch (InterruptedException e) {
			log.error("Error while sleeping", e);
		}
	}
	
	private Optional<Throwable> checkUpdateStatus(Bet bet) {
		var registeredBetStatusIn = RegisteredBetStatus.getRegisteredBetStatus(bet.status());
		var registeredBetStatusOut = betStatusRegister.compute(bet.id(), remappingFunction(registeredBetStatusIn));
		if (registeredBetStatusIn.equals(registeredBetStatusOut)) {
			return Optional.empty();
		} else {
			return Optional.of(new IllegalArgumentException(buildReason(registeredBetStatusIn, registeredBetStatusOut)));
		}
	}

	private static BiFunction<Integer, RegisteredBetStatus, RegisteredBetStatus> remappingFunction(RegisteredBetStatus registeredBetStatusIn) {
		return (key, currentRegisteredBetStatus) -> isValidStatus(currentRegisteredBetStatus, registeredBetStatusIn.betStatus()) ? registeredBetStatusIn : currentRegisteredBetStatus;
	}
	
	private static boolean isValidStatus(RegisteredBetStatus currentRegisteredBetStatus, BetStatus betStatusIn) {
		if (currentRegisteredBetStatus == null) {
			return betStatusIn == BetStatus.OPEN;
		} else {
			return currentRegisteredBetStatus.betStatus() == BetStatus.OPEN 
					&& betStatusIn != BetStatus.OPEN;
		}
	}

	private static Optional<BigDecimal> calculateResult(Bet bet) {
		return switch (bet.status()) {
			case BetStatus.OPEN -> Optional.empty();
			case BetStatus.VOID -> Optional.of(BigDecimal.ZERO);
			case BetStatus.WINNER -> Optional.of(calculateWinnings(bet));
			case BetStatus.LOSER -> Optional.of(BigDecimal.valueOf(bet.amount()).negate());  		
		};
	}

	private static BigDecimal calculateWinnings(Bet bet) {
		BigDecimal wonPercentage = BigDecimal.ONE.subtract(BigDecimal.valueOf(bet.odds()), MathContext.DECIMAL32);
		return BigDecimal.valueOf(bet.amount()).multiply(wonPercentage, MathContext.DECIMAL32);
	}
	
	private static String buildReason(RegisteredBetStatus registeredBetStatusIn, RegisteredBetStatus registeredBetStatusOut) {
		return new StringBuilder("Reason: StatusIn [")
				.append(registeredBetStatusIn.betStatus())
				.append("] , StatusOut[")
				.append(registeredBetStatusOut == null ? "null" : registeredBetStatusOut.betStatus())
				.append("]")
				.toString();
	}

	private record RegisteredBetStatus(BetStatus betStatus, UUID id) {
		static RegisteredBetStatus getRegisteredBetStatus(BetStatus betStatus) {
			return new RegisteredBetStatus(betStatus, UUID.randomUUID());
		}
	}

}

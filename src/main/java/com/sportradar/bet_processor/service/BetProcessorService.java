package com.sportradar.bet_processor.service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.sportradar.bet_processor.domain.Bet;

public interface BetProcessorService {

	/**
	 * It process a bet.
	 *
	 * @param bet bet
	 * @return the final amount won (if it is positive) or lost (if it is negative) for the client in this resolved bet. It can be empty in case of a VOID bet.
	 * 	It will complete exceptionally (IllegalArgumentException) if there is an error during processing 
	 */
	CompletableFuture<Optional<BigDecimal>> processBet(Bet bet);
	
}

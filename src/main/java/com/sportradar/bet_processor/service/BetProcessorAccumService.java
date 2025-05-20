package com.sportradar.bet_processor.service;

import com.sportradar.bet_processor.domain.Bet;

public interface BetProcessorAccumService {

	/**
	 * It adds a bet to be processed.
	 *
	 * @param bet bet
	 * @throws IllegalArgumentException in case of a validation error in the bet input
	 * @throws RejectedExecutionException in case processor is shutting-down or it can't accept any more bet for now
	 */
	void addBet(Bet bet);
	
	/**
	 * It gracefully shutdowns the whole bet processor system.
	 * 
	 * @throws IllegalArgumentException in case a shutdown is not possible because the system is starting up yet
	 */
	void shutdown();
}

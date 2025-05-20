package com.sportradar.bet_processor.service;

import java.math.BigDecimal;

import com.sportradar.bet_processor.domain.Bet;

public interface BetResultService {

	/**
	 * It processes the result for a bet.
	 *
	 * @param bet bet
	 * @param result bet result
	 */
	void processBetResult(Bet bet, BigDecimal result);
	
	/**
	 *	It processes the exception throws during the bet processing.
	 *
	 * @param bet bet
	 * @param exception exception
	 */
	void processException(Bet bet, Throwable exception);
	
	/**
	 * It gets a summary of every bet result processed.
	 *
	 * @return the summary
	 */
	String getSummary();	
}

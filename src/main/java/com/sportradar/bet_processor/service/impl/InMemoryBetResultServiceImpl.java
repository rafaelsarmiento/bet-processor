package com.sportradar.bet_processor.service.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetResultService;

/**
 * In-memory implementation to accumulate the result for the processed bets.
 * The exceptions are just logged.
 * 
 */
@Service
public class InMemoryBetResultServiceImpl implements BetResultService {

	@Override
	public void processBetResult(Bet bet, BigDecimal result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processException(Bet bet, Throwable exception) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getSummary() {
		// TODO Auto-generated method stub
		return null;
	}

}

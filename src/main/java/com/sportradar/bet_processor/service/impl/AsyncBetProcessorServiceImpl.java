package com.sportradar.bet_processor.service.impl;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetProcessorService;

/**
 * Asynchronous implementation of a worker processing a bet
 * 
 */
@Service
public class AsyncBetProcessorServiceImpl implements BetProcessorService {

	@Override
	public CompletableFuture<Optional<BigDecimal>> processBet(Bet bet) {
		// TODO Auto-generated method stub
		return null;
	}

}

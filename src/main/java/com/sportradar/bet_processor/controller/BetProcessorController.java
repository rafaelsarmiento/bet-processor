package com.sportradar.bet_processor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetProcessorAccumService;

@RestController
@RequestMapping("/bet-processor/api/v1")
public class BetProcessorController {
	
	private final Logger log = LoggerFactory.getLogger(BetProcessorController.class);
	
	private final BetProcessorAccumService betProcessorAccumService;
	
	public BetProcessorController(BetProcessorAccumService betProcessorAccumService) {
		this.betProcessorAccumService = betProcessorAccumService;
	}

	@PostMapping("/bets")
	public void addBet(@RequestBody Bet bet) {
		log.info("Bet received: {}", bet);
		betProcessorAccumService.addBet(bet);
	}
	
	@PostMapping("/shutdown")
	public void shutdown() {
		log.info("Shutdown request received");
		betProcessorAccumService.shutdown();
	}
}

package com.sportradar.bet_processor.init;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.RejectedExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetProcessorAccumService;

@Component
public class ProcessInitialBets implements CommandLineRunner {

	private final Logger log = LoggerFactory.getLogger(ProcessInitialBets.class);
	
	private final Resource resource;
	private final ObjectMapper objectMapper;
	private final BetProcessorAccumService betProcessorAccumService;
	
	public ProcessInitialBets(@Value("classpath:initial-bets.json") Resource resource, 
			ObjectMapper objectMapper,
			BetProcessorAccumService betProcessorAccumService) {
		this.resource = resource;
		this.objectMapper = objectMapper;
		this.betProcessorAccumService = betProcessorAccumService;
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting to load the initial bets ...");
		Bet[] bets = objectMapper.readValue(resource.getFile(), Bet[].class);
		Arrays.stream(bets)
			.forEach(this::processBet);
	}
	
	private void processBet(Bet bet) {
		try {
			betProcessorAccumService.addBet(bet);
		} catch (IllegalArgumentException | RejectedExecutionException e) {
			log.info("Error trying to add a bet. It can be that the queue is fulled already or a validation error. Bet [{}] status [{}]", bet.id(), bet.status(), e);
			// Probably it is because the "spring.task.execution.pool.core-size" or "spring.task.execution.pool.queue-capacity" Let's wait a second to clean the queue a bit
			try {
				Thread.sleep(Duration.ofSeconds(1));
			} catch (InterruptedException e1) {
				log.error("Error while sleeping", e);
			}
		}
	}

}

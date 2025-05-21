package com.sportradar.bet_processor.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.service.BetProcessorAccumService;

@RestController
@RequestMapping("/bet-processor/api/v1")
public class BetProcessorController implements ApplicationContextAware {
	
	private final Logger log = LoggerFactory.getLogger(BetProcessorController.class);
	
	private final BetProcessorAccumService betProcessorAccumService;
	private ApplicationContext applicationContext;
	
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
		runShutdownTask();
	}

	private void runShutdownTask() {
		// It is executed async and sleeping 2 seconds to allow the REST call to finish
		Runnable shutdownTask = () -> {
			try {
				Thread.sleep(Duration.ofSeconds(2));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SpringApplication.exit(applicationContext);
		};		
		new Thread(shutdownTask).start();  
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}

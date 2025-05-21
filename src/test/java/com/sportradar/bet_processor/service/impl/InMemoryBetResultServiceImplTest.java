package com.sportradar.bet_processor.service.impl;

import java.math.BigDecimal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.sportradar.bet_processor.domain.Bet;
import com.sportradar.bet_processor.domain.BetStatus;
import com.sportradar.bet_processor.service.BetResultService;

public class InMemoryBetResultServiceImplTest {

	private static final String CLIENT = "testClient";
	
	@Test
	public void processException() {
		
		BetResultService betResultService = createBetResultService();
		betResultService.processException(createBet(12.34), new IllegalArgumentException("Error"));
	}
	
	@Test
	public void processBetResult_firstClient_returnClientData() {
		
		double amount = 45.912;
		BigDecimal result = new BigDecimal("1.088");
		BetResultService betResultService = createBetResultService();
		betResultService.processBetResult(createBet(amount), result);
		
		String summary = betResultService.getSummary();
		Assertions.assertTrue(summary.contains("Total number of bets = 1"));
		Assertions.assertTrue(summary.contains("Total bet amount = " + amount));
		Assertions.assertTrue(summary.contains("Total profit = " + result.toPlainString()));
		Assertions.assertTrue(summary.contains(CLIENT + "=" + result.toPlainString()));
	}
	
	@Test
	public void processBetResult_sameClient_returnClientData() {
		
		double amount = 45.912;
		double amount2 = 1.088;
		BigDecimal result = new BigDecimal("1.088");
		BigDecimal result2 = new BigDecimal("5.023");
		BetResultService betResultService = createBetResultService();
		betResultService.processBetResult(createBet(amount), result);
		betResultService.processBetResult(createBet(amount2), result2);
		
		String summary = betResultService.getSummary();
		BigDecimal finalResult = result.add(result2);
		Assertions.assertTrue(summary.contains("Total number of bets = 2"));
		Assertions.assertTrue(summary.contains("Total bet amount = " + (amount + amount2)));
		Assertions.assertTrue(summary.contains("Total profit = " + finalResult.toPlainString()));
		Assertions.assertTrue(summary.contains(CLIENT + "=" + finalResult.toPlainString()));
	}
	
	@Test
	public void processBetResult_differentClients_returnClientsData() {
		
		double amount = 0.753;
		double amount2 = 543.12345;
		BigDecimal result = new BigDecimal("0.088123");
		BigDecimal result2 = new BigDecimal("457.123");
		String client2 = CLIENT + "2";
		BetResultService betResultService = createBetResultService();
		betResultService.processBetResult(createBet(amount), result);
		betResultService.processBetResult(createBet(amount2, client2), result2);
		
		String summary = betResultService.getSummary();
		Assertions.assertTrue(summary.contains("Total number of bets = 2"));
		Assertions.assertTrue(summary.contains("Total bet amount = 543.87645"));
		Assertions.assertTrue(summary.contains("Total profit = 457.211123"));
		Assertions.assertTrue(summary.contains(CLIENT + "=" + result.toPlainString()));
		Assertions.assertTrue(summary.contains(client2 + "=" + result2.toPlainString()));
	}
	
	@Test
	public void processBetResult_differentClientsBothAdding_returnClientsData() {
		
		double amount = 0.753;
		double amount2 = 543.12345;
		double amount3 = 0.88;
		double amount4 = 11.247;
		BigDecimal result = new BigDecimal("0.088123");
		BigDecimal result2 = new BigDecimal("457.123");
		BigDecimal result3 = new BigDecimal("-6.877");
		BigDecimal result4 = new BigDecimal("0.911877");
		String client2 = CLIENT + "2";
		BetResultService betResultService = createBetResultService();
		betResultService.processBetResult(createBet(amount), result);
		betResultService.processBetResult(createBet(amount2, client2), result2);
		betResultService.processBetResult(createBet(amount3, client2), result3);
		betResultService.processBetResult(createBet(amount4), result4);
		
		String summary = betResultService.getSummary();
		Assertions.assertTrue(summary.contains("Total number of bets = 4"));
		Assertions.assertTrue(summary.contains("Total bet amount = 556.00345"));
		Assertions.assertTrue(summary.contains("Total profit = 451.246000"));
		Assertions.assertTrue(summary.contains(CLIENT + "=1.000000"));
		Assertions.assertTrue(summary.contains(client2 + "=450.246"));
	}
		
	@Test
	public void processBetResult_moreThan5Clients_returnClientsData() {
		
		double amount = 0.753;
		double amount2 = 543.12345;
		double amount3 = 0.88;
		double amount4 = 11.247;
		double amount5 = 8;
		double amount6 = 1;
		double amount7 = 2;
		BigDecimal result = new BigDecimal("0.088123");
		BigDecimal result2 = new BigDecimal("457.123");
		BigDecimal result3 = new BigDecimal("-6.877");
		BigDecimal result4 = new BigDecimal("0.911877");
		BigDecimal result5 = new BigDecimal("-400.2");
		BigDecimal result6 = new BigDecimal("-10");
		BigDecimal result7 = new BigDecimal("5.96");
		String client2 = CLIENT + "2";
		String client3 = CLIENT + "3";
		String client4 = CLIENT + "4";
		String client5 = CLIENT + "5";
		String client6 = CLIENT + "6";
		String client7 = CLIENT + "7";
		BetResultService betResultService = createBetResultService();
		betResultService.processBetResult(createBet(amount), result);
		betResultService.processBetResult(createBet(amount2, client2), result2);
		betResultService.processBetResult(createBet(amount3, client3), result3);
		betResultService.processBetResult(createBet(amount4, client4), result4);
		betResultService.processBetResult(createBet(amount5, client5), result5);
		betResultService.processBetResult(createBet(amount6, client6), result6);
		betResultService.processBetResult(createBet(amount7, client7), result7);
		
		String summary = betResultService.getSummary();
		Assertions.assertTrue(summary.contains("Total number of bets = 7"));
		Assertions.assertTrue(summary.contains("Total bet amount = 567.00345"));
		Assertions.assertTrue(summary.contains("Total profit = 47.006000"));
		Assertions.assertTrue(summary.contains("Highest winners = [testClient3=-6.877, testClient=0.088123, testClient4=0.911877, testClient7=5.96, testClient2=457.123]"));
		Assertions.assertTrue(summary.contains("Highest losers = [testClient5=-400.2, testClient6=-10, testClient3=-6.877, testClient=0.088123, testClient4=0.911877]"));
	}
	
	@Test
	public void destroy() throws Exception {
		InMemoryBetResultServiceImpl betResultService = new InMemoryBetResultServiceImpl();
		betResultService.destroy();
	}
	
	private BetResultService createBetResultService() {
		return new InMemoryBetResultServiceImpl();
	}
	
	private static Bet createBet(double amount) {
		return createBet(amount, CLIENT);
	}
	
	private static Bet createBet(double amount, String client) {
		return new Bet(123, amount, 0.3, client, null, null, null, BetStatus.WINNER);
	}
	
}

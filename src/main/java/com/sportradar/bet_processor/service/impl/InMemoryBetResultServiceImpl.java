package com.sportradar.bet_processor.service.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private final Logger log = LoggerFactory.getLogger(InMemoryBetResultServiceImpl.class);

	private final Map<String, ClientBetResult> betResultByClients;
	
	public InMemoryBetResultServiceImpl() {
		betResultByClients = new ConcurrentHashMap<>();
	}
	
	@Override
	public void processBetResult(Bet bet, BigDecimal result) {
		betResultByClients.compute(bet.client(), remappingFunction(bet, result));
	}

	private BiFunction<String, ClientBetResult, ClientBetResult> remappingFunction(Bet bet, BigDecimal result) {
		return (key, currentClientBetResult) -> {
			if (currentClientBetResult == null) {
				return new ClientBetResult(1, BigDecimal.valueOf(bet.amount()), result);
			} else {
				return currentClientBetResult.addBetResult(bet.amount(), result);
			}
		};
	}

	@Override
	public void processException(Bet bet, Throwable exception) {
		log.error("Error processing bet with id [{}] because of [{}]", bet.id(), exception.getMessage());
	}

	@Override
	public synchronized String getSummary() {
		return new Summary(betResultByClients).toString();
	}
	
	private static class ClientBetResult {
		final int numBets;
		final BigDecimal totalBetAmount;
		final BigDecimal totalProfit;
		
		ClientBetResult(int numBets, BigDecimal betAmount, BigDecimal result) {
			this.numBets = numBets;
			this.totalBetAmount = betAmount;
			this.totalProfit = result;			
		}
		
		ClientBetResult addBetResult(double betAmount, BigDecimal result) {
			BigDecimal updatedBetAmount = this.totalBetAmount.add(BigDecimal.valueOf(betAmount));
			BigDecimal updatedProfit = this.totalProfit.add(result);
			return new ClientBetResult(numBets + 1, updatedBetAmount, updatedProfit);
		}
	}
	
	private static class Summary {
		private static final int HIGUEST_LIST_SIZE = 5;
		
		int numBets;
		BigDecimal totalBetAmount;
		BigDecimal totalProfit;
		final List<ClientSummaryProfit> highestWinners;
		final List<ClientSummaryProfit> highestLosers;
		
		Summary(Map<String, ClientBetResult> betResultByClients) {
			numBets = 0;
			totalBetAmount = BigDecimal.ZERO;
			totalProfit = BigDecimal.ZERO;
			List<ClientSummaryProfit> clientSummaryProfits = new LinkedList<>();
			for (var clientBetResult : betResultByClients.entrySet()) {
				numBets += clientBetResult.getValue().numBets;
				totalBetAmount = totalBetAmount.add(clientBetResult.getValue().totalBetAmount);
				totalProfit = totalProfit.add(clientBetResult.getValue().totalProfit);
				clientSummaryProfits.add(new ClientSummaryProfit(clientBetResult.getKey(), clientBetResult.getValue().totalProfit));
			}
			clientSummaryProfits = clientSummaryProfits.stream()
					.sorted((o1, o2) -> o1.profit.compareTo(o2.profit))
					.toList();
			if (clientSummaryProfits.size() <= HIGUEST_LIST_SIZE) {
				highestLosers = clientSummaryProfits;
				highestWinners = clientSummaryProfits;
			} else {
				highestLosers = clientSummaryProfits.subList(0, HIGUEST_LIST_SIZE);
				highestWinners = clientSummaryProfits.subList(clientSummaryProfits.size() - HIGUEST_LIST_SIZE, clientSummaryProfits.size());				
			}
		}
		
		@Override
		public final String toString() {
			return new StringBuilder("Summary: \n")
					.append("Total number of bets = ")
					.append(numBets)
					.append("\n")
					.append("Total bet amount = ")
					.append(totalBetAmount.toPlainString())
					.append("\n")
					.append("Total profit = ")
					.append(totalProfit.toPlainString())
					.append("\n")
					.append("Highest winners = ")
					.append(Arrays.toString(highestWinners.toArray()))
					.append("\n")
					.append("Highest losers = ")
					.append(Arrays.toString(highestLosers.toArray()))
					.append("\n")
					.toString();
		}
	}
	
	private record ClientSummaryProfit(String client, BigDecimal profit) {
		
		@Override
		public final String toString() {
			return client + "=" + profit.toPlainString();
		}
		
	}

}

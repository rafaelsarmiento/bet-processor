package com.sportradar.bet_processor.domain;

public record Bet(int id, double amount, double odds, String client, String event, String market, String selection, BetStatus status) {

}

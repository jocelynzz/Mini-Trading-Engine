package com.uchicago.jocelynz;

import java.util.List;

import com.uchicago.jocelynz.Constants.Ticker;

public class StockDataSubscriptionRequest {
	private List<Ticker> tickers;
	private String topic;
	
	public List<Ticker> getTickers() {
		return tickers;
	}
	public void setTickers(List<Ticker> tickers) {
		this.tickers = tickers;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
}

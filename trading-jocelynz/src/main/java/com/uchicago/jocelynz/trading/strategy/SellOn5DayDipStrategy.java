package com.uchicago.jocelynz.trading.strategy;

import com.uchicago.jocelynz.PriceData;

public class SellOn5DayDipStrategy implements SellStrategy {
	
	private PriceData d0;
	private PriceData d1;
	private PriceData d2;
	private PriceData d3;
	private PriceData d4;

	public SellOn5DayDipStrategy(PriceData d0, PriceData d1, PriceData d2, PriceData d3, PriceData d4) {
		this.d0 = d0;
		this.d1 = d1;
		this.d2 = d2;
		this.d3 = d3;
		this.d4 = d4;
		
	}
	
	public boolean shouldSell(float price) {
		return d0.getClose() < price && d1.getClose() < price && d2.getClose() < price 
				&& d3.getClose() < price && d4.getClose() < price;
	}

}

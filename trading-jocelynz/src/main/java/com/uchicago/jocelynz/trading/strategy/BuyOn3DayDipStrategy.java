package com.uchicago.jocelynz.trading.strategy;

import com.uchicago.jocelynz.PriceData;

public class BuyOn3DayDipStrategy implements BuyStrategy {
	
	private PriceData d0;
	private PriceData d1;
	private PriceData d2;

	public BuyOn3DayDipStrategy(PriceData d0, PriceData d1, PriceData d2) {
		this.d0 = d0;
		this.d1 = d1;
		this.d2 = d2;
		
	}

	public boolean shouldBuy() {
		return d1.getClose() < d0.getClose() &&
				d2.getClose() < d1.getClose() && 
				(d0.getClose() - d2.getClose()) / d0.getClose() > 0.1;
	}

}

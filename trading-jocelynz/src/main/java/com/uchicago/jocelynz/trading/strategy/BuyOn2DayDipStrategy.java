package com.uchicago.jocelynz.trading.strategy;

import com.uchicago.jocelynz.PriceData;

public class BuyOn2DayDipStrategy implements BuyStrategy {
	
	private PriceData d0;
	private PriceData d1;

	public BuyOn2DayDipStrategy(PriceData d0, PriceData d1) {
		this.d0 = d0;
		this.d1 = d1;
		
	}

	public boolean shouldBuy() {
		return d1.getClose() < d0.getClose() && (d0.getClose() - d1.getClose()) / d0.getClose() > 0.05;
	}

}

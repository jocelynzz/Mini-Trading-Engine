package com.uchicago.jocelynz.trading.traders;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.uchicago.jocelynz.Constants.Ticker;
import com.uchicago.jocelynz.PriceData;
import com.uchicago.jocelynz.StockDataSubscriptionRequest;
import com.uchicago.jocelynz.StockInventory;
import com.uchicago.jocelynz.iterator.FIFOStockIterator;
import com.uchicago.jocelynz.iterator.StockInventoryIterator;
import com.uchicago.jocelynz.trading.strategy.BuyOn2DayDipStrategy;
import com.uchicago.jocelynz.trading.strategy.BuyOn3DayDipStrategy;

public class Jocelyn extends Trader {
	
	private static final int BUY_ORDER_SIZE = 100;
	
	private Map<Ticker, List<PriceData>> pastData = new HashMap<Ticker, List<PriceData>>();
	private List<StockInventory> stockInventory = new ArrayList<StockInventory>();
	
	public Jocelyn(int startingCapital) {
		super(startingCapital);
	}
	
	@Override
	public StockInventoryIterator getStockInventoryIterator() {
		return new FIFOStockIterator(stockInventory);
	}

	@Override
	public StockDataSubscriptionRequest createDataSubscriptionRequest() {
		StockDataSubscriptionRequest r = new StockDataSubscriptionRequest();
		r.setTickers(Arrays.asList(Ticker.LNKD, Ticker.AAPL));
		r.setTopic(getDataTopicName());
		return r;
	}

	@Override
	public boolean shouldBuy(PriceData pricingData) {
		if (pricingData.getClose() * BUY_ORDER_SIZE > getCashPosition()) {
			// cannot afford to buy
			return false;
		}
		
		List<PriceData> pastThreeDaysData = pastData.get(pricingData.getTicker());
		// flag to switch to a more conservative buying strategy if loss is greater than 10 percent
		boolean isPessimistic = (getStockPrincipalValue() - getStockMarketValue()) / getStockPrincipalValue() > 0.1;
		
		if (pastThreeDaysData == null) {
			return true;
		} else if (pastThreeDaysData.size() == 3) {
			
			if (isPessimistic) {
				return new BuyOn3DayDipStrategy(pastThreeDaysData.get(0), pastThreeDaysData.get(1), pastThreeDaysData.get(2)).shouldBuy();
			} else {
				return new BuyOn2DayDipStrategy(pastThreeDaysData.get(0), pastThreeDaysData.get(1)).shouldBuy();
			}
		}
		
		return false;
	}

	/**
	 * Jocelyn goes long, never sells
	 */
	@Override
	public boolean shouldSell(PriceData pricingData) {
		return false;
	}

	@Override
	public int makeBuyOrder(Ticker t, float price, long time) {
		StockInventory inventory = new StockInventory();
		inventory.setTicker(t);
		inventory.setPrice(price);
		inventory.setNumShares(BUY_ORDER_SIZE);
		inventory.setTime(time);
		Date d = new Date(time * 10000);
		stockInventory.add(inventory);
		System.out.println("Jocelyn bought " + BUY_ORDER_SIZE + " shares of " + t + " at " + price
				+ " on " + d);
		return BUY_ORDER_SIZE;
	}

	/**
	 * Jocelyn goes long, never sells
	 */
	@Override
	public int makeSellOrder(Ticker t, float price, long time) {
		return 0;
	}

	@Override
	public String getDataTopicName() {
		return "jocelynz-daily-pricing-data";
	}

	@Override
	public void addPricingDataPoint(PriceData pricingData) {
		Ticker ticker = pricingData.getTicker();
		if (pastData.get(ticker) == null) {
			pastData.put(ticker, new LinkedList<PriceData>());
		}
		
		if (pastData.get(ticker).size() == 3) {
			pastData.get(ticker).remove(0);
		}
		
		pastData.get(ticker).add(pricingData);
	}

	@Override
	public String getName() {
		return "Jocelyn Zhang";
	}

}

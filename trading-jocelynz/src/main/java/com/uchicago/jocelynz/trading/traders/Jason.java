package com.uchicago.jocelynz.trading.traders;

import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.uchicago.jocelynz.Constants.Ticker;
import com.uchicago.jocelynz.PriceData;
import com.uchicago.jocelynz.StockDataSubscriptionRequest;
import com.uchicago.jocelynz.StockInventory;
import com.uchicago.jocelynz.iterator.LIFOStockIterator;
import com.uchicago.jocelynz.iterator.StockInventoryIterator;
import com.uchicago.jocelynz.trading.strategy.SellOn5DayDipStrategy;


public class Jason extends Trader {
	
	private static final int SELL_ORDER_SIZE = 100;
	private static final int BUY_ORDER_SIZE = 1500;
	
	private Map<Ticker, List<PriceData>> pastData = new HashMap<Ticker, List<PriceData>>();
	private List<StockInventory> stockInventory = new ArrayList<StockInventory>();
	
	
	public Jason(int startingCapital) {
		super(startingCapital);
	}
	
	@Override
	public StockInventoryIterator getStockInventoryIterator() {
		return new LIFOStockIterator(stockInventory);
	}

	@Override
	public StockDataSubscriptionRequest createDataSubscriptionRequest() {
		StockDataSubscriptionRequest r = new StockDataSubscriptionRequest();
		r.setTickers(Arrays.asList(Ticker.MSFT, Ticker.T));
		r.setTopic(getDataTopicName());
		return r;
	}

	@Override
	public boolean shouldSell(PriceData pricingData) {
		Ticker t = pricingData.getTicker();
		int numShares = 0;
		float price = 0;
		
		if (t.equals(Ticker.T)) {
			numShares = stockInventory.get(0).getNumShares();	
			price = stockInventory.get(0).getPrice();
		}
		
		if (t.equals(Ticker.MSFT)) {
			numShares = stockInventory.get(1).getNumShares();	
			price = stockInventory.get(1).getPrice();
		}
		
		if (numShares < SELL_ORDER_SIZE ) {
			// donnot have enough inventory to sell
			return false;
		}
		
		List<PriceData> pastFiveDaysData = pastData.get(pricingData.getTicker());
		
		if (pastFiveDaysData == null) {
			return true;
		} else if (pastFiveDaysData.size() == 5) {
			return new SellOn5DayDipStrategy(pastFiveDaysData.get(0), pastFiveDaysData.get(1),
					pastFiveDaysData.get(2), pastFiveDaysData.get(3), pastFiveDaysData.get(4)).shouldSell(price);			
		}
		
		return false;
	}

	/**
	 * Jason goes short, never buys
	 * 
	 */
	@Override
	public boolean shouldBuy(PriceData pricingData) {
		Ticker t = pricingData.getTicker();
		for (StockInventory inventory : stockInventory) {
			if (inventory.getTicker().equals(t)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int makeBuyOrder(Ticker t, float price, long time) {
		StockInventory inventory = new StockInventory();
		inventory.setTicker(t);
		inventory.setPrice(price);
		inventory.setNumShares(BUY_ORDER_SIZE);
		inventory.setTime(time);
		Date d = new Date(time * 1000);
		stockInventory.add(inventory);
		System.out.println("Jason bought " + BUY_ORDER_SIZE + " shares of " + t + " at " + price
				+ " on " + d);
		return BUY_ORDER_SIZE;
	}

	/**
	 * Jason goes short, never buys
	 */
	@Override
	public int makeSellOrder(Ticker t, float price, long time) {
		if (t.equals(Ticker.T)) {
			int numShares = stockInventory.get(0).getNumShares();
			int updatedShares = numShares - SELL_ORDER_SIZE;
			stockInventory.get(0).setNumShares(updatedShares);			
		}
		if (t.equals(Ticker.MSFT)) {
			int numShares = stockInventory.get(1).getNumShares();
			int updatedShares = numShares - SELL_ORDER_SIZE;
			stockInventory.get(1).setNumShares(updatedShares);			
		}
		Date f = new Date(time * 1000);
		System.out.println("Jason sold " + SELL_ORDER_SIZE + " shares of " + t + " at " + price
				+ " on " + f);
		return SELL_ORDER_SIZE;

	}

	@Override
	public String getDataTopicName() {
		return "Jason-daily-pricing-data";
	}

	@Override
	public void addPricingDataPoint(PriceData pricingData) {
		Ticker ticker = pricingData.getTicker();
		if (pastData.get(ticker) == null) {
			pastData.put(ticker, new LinkedList<PriceData>());
		}
		
		if (pastData.get(ticker).size() == 5) {
			pastData.get(ticker).remove(0);
		}
		
		pastData.get(ticker).add(pricingData);
	}

	@Override
	public String getName() {
		return "Jason Wu";
	}

}

package com.uchicago.jocelynz.trading.traders;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicago.jocelynz.Constants.Ticker;
import com.uchicago.jocelynz.PriceData;
import com.uchicago.jocelynz.StockDataSubscriptionRequest;
import com.uchicago.jocelynz.StockInventory;
import com.uchicago.jocelynz.Utils;
import com.uchicago.jocelynz.iterator.StockInventoryIterator;
import com.uchicago.jocelynz.trading.control.ReportingEngine;
import com.uchicago.jocelynz.trading.control.TraderControlRequest;
import com.uchicago.jocelynz.trading.control.TraderControlRequestListener;
import com.uchicago.jocelynz.trading.control.TraderControls;

// template
// observers
public abstract class Trader implements TraderControlRequestListener {

	private double cashPosition;
	private Map<Ticker, Float> marketPrice;

	public Trader(double startingCapital) {
		cashPosition = startingCapital;
		marketPrice = new HashMap<Ticker, Float>();
	}
	
	/**
	 * receive stock pricing data
	 */
	public void init() throws JsonProcessingException, JMSException {
		StockDataSubscriptionRequest request = createDataSubscriptionRequest();
		Utils.subscribeToStockData(request);
	}
	
	public void start() throws Exception {
		CamelContext context = new DefaultCamelContext();
		context.addComponent("jms", Utils.createJmsComponent());
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				from("jms:topic:" + getDataTopicName()).process(new Processor() {
					
					public void process(Exchange exchange) throws Exception {
						String data = exchange.getIn().getBody(String.class);
						PriceData pricingData = new ObjectMapper().readValue(data, PriceData.class);
						processDailyPricingData(pricingData);
					}
				});
			}
		});
		context.start();
	}
	
	/**
	 * template method to process each incoming pricing data point
	 */
	public void processDailyPricingData(PriceData pricingData) {
		Ticker ticker = pricingData.getTicker();
		marketPrice.put(ticker, pricingData.getAdjClose());
		addPricingDataPoint(pricingData);
		
		if (shouldBuy(pricingData)) {
			int numBought = makeBuyOrder(ticker, marketPrice.get(ticker), pricingData.getDate());
			cashPosition -= numBought * marketPrice.get(ticker);
			System.out.println("Bought shares of " + ticker + ". Account total cash value: " + getCashPosition() + " stock value: " + getStockMarketValue());
			
		} else if (shouldSell(pricingData)) {
			int numSold = makeSellOrder(ticker, marketPrice.get(ticker), pricingData.getDate());
			cashPosition += numSold * marketPrice.get(ticker);
			System.out.println("Sold shares of " + ticker + ". Account total cash value: " + getCashPosition() + " stock value: " + getStockMarketValue());
		}
	}
	
	/**
	 * returns the market value of the stock holdings
	 */
	public double getStockMarketValue() {
		StockInventoryIterator itr = getStockInventoryIterator();
		double value = 0;
		while (itr.hasNext()) {
			StockInventory inventory = itr.next();
			value += marketPrice.get(inventory.getTicker()) * inventory.getNumShares();
		}
		return value;
	}
	
	/**
	 * returns amount paid to own the stock holdings
	 */
	public double getStockPrincipalValue() {
		StockInventoryIterator itr = getStockInventoryIterator();
		double value = 0;
		while (itr.hasNext()) {
			StockInventory inventory = itr.next();
			value += inventory.getPrice() * inventory.getNumShares();
		}
		return value;
	}
	
	/**
	 * adds a daily pricing data point
	 */
	public abstract void addPricingDataPoint(PriceData pricingData);

	/**
	 * returns the topic name where the trader can expect to find subscribed
	 * daily pricing data
	 */
	public abstract String getDataTopicName();
	
	/**
	 * returns an iterator that one can use to look into the trader's stock holdings
	 */
	public abstract StockInventoryIterator getStockInventoryIterator();
	
	/**
	 * creates a stock data subscription request that relects the interested
	 * daily pricing data of this trader
	 */
	public abstract StockDataSubscriptionRequest createDataSubscriptionRequest();
	
	/**
	 * returns true if a purchase should be made on the given stock
	 */
	public abstract boolean shouldBuy(PriceData d);
	
	/**
	 * returns true if a sale should be made on the given stock
	 */
	public abstract boolean shouldSell(PriceData d);
	
	/**
	 * adds the stock inventory to its holdings, and then return the number of shares
	 * added
	 */
	public abstract int makeBuyOrder(Ticker t, float purchasPrice, long time);
	
	/**
	 * reduces the stock inventory from its holdings, and then return the number of shares
	 * removed
	 */
	public abstract int makeSellOrder(Ticker t, float salePrice, long time);

	public double getCashPosition() {
		return cashPosition;
	}
	
	public void traderControlRequestReceived(TraderControlRequest request) {
		if (request.getControl() == TraderControls.ACCOUNT_SUMMARY) {
			ReportingEngine.getInstance().printAccountSummary(getName(), getStockInventoryIterator(), getCashPosition(), getStockPrincipalValue(), getStockMarketValue());
		} else {
			System.out.println("Unimplemented trader control " + request.getControl());
		}
	}
	
	public abstract String getName();


}

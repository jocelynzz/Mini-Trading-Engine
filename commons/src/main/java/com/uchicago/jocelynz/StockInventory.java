package com.uchicago.jocelynz;

import com.uchicago.jocelynz.Constants.Ticker;

public class StockInventory {

	private Ticker ticker;
	private float price;
	private int numShares;
	private long time;
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public int getNumShares() {
		return numShares;
	}
	public void setNumShares(int numShares) {
		this.numShares = numShares;
	}
	public Ticker getTicker() {
		return ticker;
	}
	public void setTicker(Ticker ticker) {
		this.ticker = ticker;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
}

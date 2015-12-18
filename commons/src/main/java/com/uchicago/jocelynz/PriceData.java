package com.uchicago.jocelynz;

import com.uchicago.jocelynz.Constants.Ticker;

public class PriceData {
	
	private Ticker ticker;
	private long date;
	private float open;
	private float high;
	private float low;
	private float close;
	private long volume;
	private float adjClose;
	
	public Ticker getTicker() {
		return ticker;
	}
	public void setTicker(Ticker ticker) {
		this.ticker = ticker;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public long getVolume() {
		return volume;
	}
	public void setVolume(long volume) {
		this.volume = volume;
	}
	public float getAdjClose() {
		return adjClose;
	}
	public void setAdjClose(float adjClose) {
		this.adjClose = adjClose;
	}
}

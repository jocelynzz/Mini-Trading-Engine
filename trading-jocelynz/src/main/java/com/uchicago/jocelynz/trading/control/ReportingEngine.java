package com.uchicago.jocelynz.trading.control;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.uchicago.jocelynz.StockInventory;
import com.uchicago.jocelynz.iterator.StockInventoryIterator;

//singleton
public final class ReportingEngine {
	
	private static final ReportingEngine instance = new ReportingEngine();
	
	private ReportingEngine() {
	}
	
	public static ReportingEngine getInstance() {
		return instance;
	}
	
	public void printAccountSummary(String name, StockInventoryIterator
			itr, double cashPosition, double stockMarketValue, double stockPrincipalValue) {
		System.out.println("========== ACCOUNT SUMMARY BEGINS ==========");
		System.out.println("Trader's Name: " + name);
		SimpleDateFormat format = new SimpleDateFormat();
		System.out.println("Current stock holdings");
		while (itr.hasNext()) {
			StockInventory i = itr.next();
			System.out.println(i.getTicker() + ": purchased on " + 
				format.format(new Date(i.getTime())) + " for " + i.getPrice() + 
				" " + i.getNumShares() + " shares");
		}
		System.out.println("Available cash position: " + cashPosition);
		System.out.println("Market value of all stock holdings: " + stockMarketValue);
		double portfolioMarketValue = cashPosition + stockMarketValue;
		System.out.println("Total market value of investments: " + portfolioMarketValue);
		double porfolioPrincipalAmount = cashPosition + stockPrincipalValue;
		double performance = (portfolioMarketValue - porfolioPrincipalAmount)/porfolioPrincipalAmount;
		System.out.println("Performance: " + performance);
		System.out.println("========== ACCOUNT SUMMARY ENDS ==========");
		
	}
}

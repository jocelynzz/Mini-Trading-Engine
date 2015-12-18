package com.uchicago.jocelynz.iterator;

import com.uchicago.jocelynz.StockInventory;

public interface StockInventoryIterator {

	public boolean hasNext();
	
	public StockInventory next();
}

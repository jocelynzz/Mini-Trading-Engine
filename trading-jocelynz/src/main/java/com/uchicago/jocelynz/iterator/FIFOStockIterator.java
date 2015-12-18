package com.uchicago.jocelynz.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.uchicago.jocelynz.StockInventory;

public class FIFOStockIterator implements StockInventoryIterator {
	
	private Iterator<StockInventory> iterator;

	public class FIFOComparator implements Comparator<StockInventory> {

		public int compare(StockInventory o1, StockInventory o2) {
			return (int) (o1.getTime() - o2.getTime());
		}
	}

	public FIFOStockIterator(Collection<StockInventory> inventories) {
		List<StockInventory> unsorted = new ArrayList<StockInventory>(inventories);
		Collections.sort(unsorted, new FIFOComparator());
		iterator = unsorted.iterator();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public StockInventory next() {
		return iterator.next();
	}

}

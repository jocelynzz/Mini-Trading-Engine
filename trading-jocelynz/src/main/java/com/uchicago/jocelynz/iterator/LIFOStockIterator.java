package com.uchicago.jocelynz.iterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.uchicago.jocelynz.StockInventory;

public class LIFOStockIterator implements StockInventoryIterator {

	private Iterator<StockInventory> iterator;

	public class LIFOComparator implements Comparator<StockInventory> {

		public int compare(StockInventory o1, StockInventory o2) {
			return (int) (o2.getTime() - o1.getTime());
		}
	}

	public LIFOStockIterator(Collection<StockInventory> inventories) {
		List<StockInventory> unsorted = new ArrayList<StockInventory>(inventories);
		Collections.sort(unsorted, new LIFOComparator());
		iterator = unsorted.iterator();
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public StockInventory next() {
		return iterator.next();
	}
}

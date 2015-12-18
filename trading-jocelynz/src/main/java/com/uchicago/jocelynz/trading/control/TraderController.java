package com.uchicago.jocelynz.trading.control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TraderController {
	
	//observer
	
	private List<TraderControlRequestListener> observers = new ArrayList<TraderControlRequestListener>();

	public void start() {
		System.out.println("Type the following commands: " + Arrays.toString(TraderControls.values()));
    	Scanner scan = new Scanner(System.in);
    	while (scan.hasNext()) {
    		String ln = scan.nextLine();
    		try {
    			TraderControls c = TraderControls.valueOf(ln);
    			TraderControlRequest r = new TraderControlRequest();
    			r.setControl(c);
    			notifyObservers(r);
    		} catch (Exception e) {
    			System.out.println("Invalid command. Available commands are: " + Arrays.toString(TraderControls.values()));
    		}
    	}
    	scan.close();
	}
	
	public void addTraderControlRequestListener(TraderControlRequestListener l) {
		observers.add(l);
	}
	
	public void removeTraderControlRequestListener(TraderControlRequestListener l) {
		observers.remove(l);
	}
	
	private void notifyObservers(TraderControlRequest request) {
		for (TraderControlRequestListener l : observers) {
			l.traderControlRequestReceived(request);
		}
	}
}

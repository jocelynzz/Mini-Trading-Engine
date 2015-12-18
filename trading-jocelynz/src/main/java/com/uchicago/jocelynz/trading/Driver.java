package com.uchicago.jocelynz.trading;

import com.uchicago.jocelynz.trading.control.TraderController;
import com.uchicago.jocelynz.trading.traders.Jocelyn;
import com.uchicago.jocelynz.trading.traders.Jason;
import com.uchicago.jocelynz.trading.traders.Trader;

public class Driver 
{
    public static void main( String[] args ) throws Exception {
    	Trader jocelyn = new Jocelyn(1000000);
    	Trader jason = new Jason(1000000);
    	jocelyn.init();
    	jason.init();
   
    	TraderController controller = new TraderController();
    	controller.addTraderControlRequestListener(jocelyn);
    	controller.addTraderControlRequestListener(jason);
    	
    	jocelyn.start();
    	jason.start();
    	
    	controller.start();
    }
}

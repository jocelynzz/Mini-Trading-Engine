package com.uchicago.jocelynz;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.camel.Component;
import org.apache.camel.main.Main;

import com.uchicago.jocelynz.Constants.Ticker;

public class Runner {
    public static void main( String[] args ) throws Exception {
    	Component jmsComponent = Utils.createJmsComponent();
    	Main main = new Main();
    	main.enableHangupSupport();
    	main.bind("jms", jmsComponent);
    	
    	main.addRouteBuilder(new StockDataConsumer(new ConcurrentHashMap<Ticker, List<String>>()));
    	main.run();
    }
}
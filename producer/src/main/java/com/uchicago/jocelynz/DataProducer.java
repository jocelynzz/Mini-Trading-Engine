package com.uchicago.jocelynz;

import java.text.SimpleDateFormat;

import org.apache.camel.Component;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.Main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicago.jocelynz.Constants.Ticker;

public class DataProducer {
	
	private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-mm-dd");
	//message translator
	private final class CSVToJsonProcessor implements Processor {
		public void process(Exchange exchange) throws Exception {
			
			String camelFileName = exchange.getIn().getHeader("CamelFileName").toString();
			Ticker ticker = Ticker.valueOf(camelFileName.split("\\.")[0]);
			String[] rawData = exchange.getIn().getBody(String.class).split(",");
			//process raw data
			PriceData pricingData = new PriceData();
			pricingData.setTicker(ticker);
			pricingData.setDate(formatter.parse(rawData[0]).getTime());
			pricingData.setOpen(Float.parseFloat(rawData[1]));
			pricingData.setHigh(Float.parseFloat(rawData[2]));
			pricingData.setLow(Float.parseFloat(rawData[3]));
			pricingData.setClose(Float.parseFloat(rawData[4]));
			pricingData.setVolume(Long.parseLong(rawData[5]));
			pricingData.setAdjClose(Float.parseFloat(rawData[6]));
			
			exchange.getIn().setBody(
					new ObjectMapper().writerWithType(PriceData.class).writeValueAsString(pricingData));
		}
	}
	
    public Main getMain() throws Exception {
    	Component component = Utils.createJmsComponent();
    	Main main = new Main();
    	main.bind("jms", component);
    	main.addRouteBuilder(new RouteBuilder() {
    		
    		//endpoint
    		//message filter
			
			@Override
			public void configure() throws Exception {
				from("file:data").split(body(String.class).tokenize("\n"))
					.choice()
						.when(simple("${property.CamelSplitIndex} > 0"))
							.process(new CSVToJsonProcessor()).to("jms:" + Constants.ALL_DATA_QUEUE_NAME)
						.otherwise();
			}
		});
    	return main;
    }
    
    public static void main(String[] args) throws Exception {
		new DataProducer().getMain().run();
	}
}

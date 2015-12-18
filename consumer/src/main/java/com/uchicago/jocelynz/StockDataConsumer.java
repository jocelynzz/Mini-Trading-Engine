package com.uchicago.jocelynz;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uchicago.jocelynz.Constants.Ticker;

public class StockDataConsumer extends RouteBuilder {
	
	final Map<Ticker, List<String>> subscribers;
	
	public StockDataConsumer(final Map<Ticker, List<String>> subscribers) {
		this.subscribers = subscribers;
	}
	
	@Override
	public void configure() throws Exception {
		
		from("jms:" + Constants.DATA_REQUEST_QUEUE_NAME).process(new Processor() {
			public void process(Exchange e) throws Exception {
				String body = e.getIn().getBody(String.class);
				ObjectMapper mapper = new ObjectMapper();
				StockDataSubscriptionRequest request = mapper.readValue(body, StockDataSubscriptionRequest.class);
				for (Ticker t : request.getTickers()) {
					if (subscribers.get(t) == null) {
						subscribers.put(t, new ArrayList<String>());
					}
					subscribers.get(t).add(request.getTopic());
				}
			}
		});
		
		from("jms:" + Constants.ALL_DATA_QUEUE_NAME).
			dynamicRouter(method(new SubscriberDynamicRouter(subscribers), "send"));
	}
	
	//Dynamic Router
	public final class SubscriberDynamicRouter {

		private Map<Ticker, List<String>> subscribers;
		private Set<String> sentMessages;

		public SubscriberDynamicRouter(Map<Ticker, List<String>> subscribers) {
			this.subscribers = subscribers;
			sentMessages = new HashSet<String>();
		}

		public String send(String msg) throws JsonProcessingException, IOException {
			if (sentMessages.contains(msg)) {
				return null;
			}
			PriceData priceData = new ObjectMapper().readValue(msg, PriceData.class);
			List<String> topics = subscribers.get(priceData.getTicker());
			if (topics == null) {
				return null;
			} else {
				String endpoint = Utils.topicNamesToEndpoint(topics);
				sentMessages.add(msg);
				return endpoint;
			}
		}
		
	}

}

package com.example;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.jms.core.JmsTemplate;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/order")
public class OrderServiceController {
	
	@RequestMapping(value = "/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMessage() {

		ResponseEntity<String> response = null;  
		response = new ResponseEntity<>("OrderService is live", HttpStatus.OK);
		return response;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<Order>> findOrders() {	
		
		ResponseEntity<ArrayList<Order>> response = null;
		ArrayList<Order> orders = new ArrayList<Order>();
		
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item("CTN-10020","10"));
		items.add(new Item("CTN-87456","20"));
		
		Order order = new Order("1001", "Sibendu Das", items, "760.50$", "Ship to default address");
		orders.add(order);
		
		try {
			System.out.println("Sending....");
			MessageSender.send(order.toString(), "ORDER_QUEUE");
			System.out.println("Message sent to queue");
		}catch(Throwable t) {			
			t.printStackTrace();
			System.out.println("Error: "+t.getMessage());
		}
		
		response = new ResponseEntity<ArrayList<Order>>(orders, HttpStatus.OK);

		return response;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> findById(
			@Parameter(description="Order Id") @PathVariable(name = "id", required = true) String id) {	
		
		
		ResponseEntity<Order> response = null;
		
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item("CTN-10020","10"));
		items.add(new Item("CTN-87456","20"));
		
		Order order = new Order("1001", "Sibendu Das", items, "760.50$", "Ship to default address");
		
		response = new ResponseEntity<Order>(order, HttpStatus.OK);

		return response;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> processOrder(@Parameter(description="Order") @RequestBody Order order) {

		ResponseEntity<Order> response = null;
		
		java.util.Random r = new java.util.Random();
		int result = r.nextInt(100000);
		
		String id = "ORD#"+result;
		order.setId(id);
		System.out.println("Order processed. Order# = "+id+"  "+order.toString());
//		System.out.println("No of items: "+order.getItems().size());
//		for (int i = 0; i < order.getItems().size(); i++) {
//			System.out.println("Item "+i+" :: "+order.getItems().get(i).getCode());
//		}
		
		try {
			System.out.println("Calling ProductService");
			
			RestTemplate rt = new RestTemplate();
            rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            rt.getMessageConverters().add(new StringHttpMessageConverter());
            
            
            String uri = "http://localhost:8082/product/";
            if(System.getenv("PRODUCT_SERVICE_URL") != null) {
        		uri = System.getenv("PRODUCT_SERVICE_URL");
        		System.out.println("PRODUCT_SERVICE_URL = "+uri);
        	}
            
            Map<String, String> vars = new HashMap<String, String>();
            ERPProductMap erpMap = rt.postForObject(uri, order , ERPProductMap.class, vars);
            
            System.out.println("Callied ProductService and mapped order items to Backend ERPs");
			
            if(erpMap != null) {
            	
            	System.out.println("For PRD = " +erpMap.getPrdProducts().size());
            	
            	if(erpMap.getMp1Products() != null && erpMap.getMp1Products().size() > 0) {
            		System.out.println("Order items being posted to MP1");
            		order.setItems(erpMap.getMp1Products());
            		MessageSender.send(order.toString(), "ORDER_MP1");
            		System.out.println("Event sent for MP1");
            	}
            	
            	if(erpMap.getPrdProducts() != null && erpMap.getPrdProducts().size() > 0) {
            		System.out.println("Order items being posted to PRD");
            		order.setItems(erpMap.getPrdProducts());
            		MessageSender.send(order.toString(), "ORDER_PRD");
            		System.out.println("Event sent for PRD");
            	}
            }
            
            System.out.println("Order Processing done");
		}catch(Throwable t) {			
			t.printStackTrace();
			System.out.println("Error: "+t.getMessage());
		}
		
		if(order.getRemarks() != null && order.getRemarks().trim().equalsIgnoreCase("error")) {
			response = new ResponseEntity<Order>(order, HttpStatus.BAD_REQUEST);
		}else {
			response = new ResponseEntity<Order>(order, HttpStatus.OK);
		}
		
		return response;
	}

}
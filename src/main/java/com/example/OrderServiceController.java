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
import org.springframework.web.bind.annotation.ResponseBody;
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

	@Autowired
	private OrderRepository orderRepository;

	public String generateOrderNo() {
		return "PILORD#" + new java.util.Random().nextInt(100000);
	}

	@RequestMapping(value = "/health", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getMessage() {
		ResponseEntity<String> response = null;
		response = new ResponseEntity<>("OrderService is live", HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ArrayList<Order>> findOrders() {
		ResponseEntity<ArrayList<Order>> response = null;
		ArrayList<Order> allOrders = new ArrayList<>();
		Iterable<Order> orders = orderRepository.findAll();
		for (Order order : orders) {
			allOrders.add(order);
		}
		System.out.println("# of orders found = " + allOrders.size());
		response = new ResponseEntity<ArrayList<Order>>(allOrders, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> findById(
			@Parameter(description = "Order Id") @PathVariable(name = "id", required = true) String id) {

		ResponseEntity<Order> response = null;
		Order result = null;
		Iterable<Order> orders = orderRepository.findAll();
		for (Order order : orders) {
			if (order.getId().equalsIgnoreCase(id)) {
				result = order;
			}
		}
		response = new ResponseEntity<Order>(result, HttpStatus.OK);
		return response;
	}

	@RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Order> processOrder(@Parameter(description = "Order") @RequestBody Order order) {

		ResponseEntity<Order> response = null;

		order.setId(generateOrderNo());
		System.out.println("Order processed. Order# = " + order.getId() + " ; payload is ::: " + order.toString());

		try {
			System.out.println("Calling ProductService");
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			String uri = "http://localhost:8082/product/";
			if (System.getenv("PRODUCT_SERVICE_URL") != null) {
				uri = System.getenv("PRODUCT_SERVICE_URL");
				System.out.println("PRODUCT_SERVICE_URL = " + uri);
			}

			Map<String, String> vars = new HashMap<String, String>();
			ERPProductMap erpMap = rt.postForObject(uri, order, ERPProductMap.class, vars);

			System.out.println("Callied ProductService and mapped order items to Backend ERPs");

			if (erpMap != null) {

				System.out.println("Saving the order");
				List<Item> orderItems = order.getItems();
				for (int i = 0; i < orderItems.size(); i++) {
					orderItems.get(i).setOrder(order);
				}
				orderRepository.save(order);
				System.out.println("Order# " + order.getId() + " saved in DB");

				System.out.println("Creating order in ERPs .. ");

				if (erpMap.getMp1Products() != null && erpMap.getMp1Products().size() > 0) {
					System.out.println("Items for Mp1 = " + erpMap.getMp1Products().size());
					order.setItems(erpMap.getMp1Products());
					MessageSender.send(order.toString(), "ORDER_MP1");
					System.out.println("Event sent for MP1");
				}

				if (erpMap.getPrdProducts() != null && erpMap.getPrdProducts().size() > 0) {
					System.out.println("Items for PRD = " + erpMap.getPrdProducts().size());
					order.setItems(erpMap.getPrdProducts());
					MessageSender.send(order.toString(), "ORDER_PRD");
					System.out.println("Event sent for PRD");
				}
			}

		} catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Error: " + t.getMessage());
		}

		if (order.getRemarks() != null && order.getRemarks().trim().equalsIgnoreCase("error")) {
			response = new ResponseEntity<Order>(order, HttpStatus.BAD_REQUEST);
		} else {
			response = new ResponseEntity<Order>(order, HttpStatus.OK);
		}

		System.out.println("Order Processed. Returning response...");
		return response;
	}

	@GetMapping(path = "/testaddorder") //
	public @ResponseBody String addOrder(@RequestParam String name, @RequestParam String price,
			@RequestParam String comment, @RequestParam String item, @RequestParam String quantity) {
		
		Order order = new Order(null, name, null, price, comment, null);
		order.setId(generateOrderNo());
		
		ArrayList<Item> items = new ArrayList<Item>();
		items.add(new Item(null, item, quantity, order));
		order.setItems(items);
		
		ArrayList<ERPOrder> erpOrders = new ArrayList<ERPOrder>();
		erpOrders.add(new ERPOrder("MP1-"+generateOrderNo(),"MP1",order,"Shipped"));
		erpOrders.add(new ERPOrder("MP1-"+generateOrderNo(),"MP1",order,"WIP"));
		erpOrders.add(new ERPOrder("PRD-"+generateOrderNo(),"PRD",order,"Shipped"));
		order.setErpOrders(erpOrders);
		
		orderRepository.save(order);
		
		return "Order Saved";
	}
	
	@GetMapping(path = "/testcleanorder") //
	public @ResponseBody String cleanOrder() {
		orderRepository.deleteAll();
		return "All orders deleted";
	}

}
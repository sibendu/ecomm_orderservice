package com.example;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class Order implements Serializable {
	
	private String id;
	private String customer;
	private List<Item> items;
	private String price;
	private String remarks;
	
	public Order() {
		super();
	}

	public Order(String id, String customer, List<Item> items, String price, String remarks) {
		super();
		this.id = id;
		this.customer = customer;
		this.items = items;
		this.price = price;
		this.remarks = remarks;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getCustomer() {
		return customer;
	}



	public void setCustomer(String customer) {
		this.customer = customer;
	}



	public List<Item> getItems() {
		return items;
	}



	public void setItems(List<Item> items) {
		this.items = items;
	}



	public String getPrice() {
		return price;
	}



	public void setPrice(String price) {
		this.price = price;
	}



	public String getRemarks() {
		return remarks;
	}



	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}


	public String toString() {
		String ord = "{"
				+ "\"id\":\"" + id + "\","
				+ "\"customer\":\"" + customer + "\","				
				+ "items{";
		
			for (int i = 0; i < items.size(); i++) {
				ord = ord + "item " + items.get(i).toString() + ", ";
			}

			ord = ord + "},"
					+ "\"price\":\"" + price + "\","
					+ "\"remarks\":\"" + remarks + "\""
					+"}";
		return ord;
	}
	
	
}

package com.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.GenericGenerator;

@Entity(name = "ORDERS")
public class Order implements Serializable {

	@Id
//	@GeneratedValue(generator = "system-uuid")
//	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	private String id;

	private String customer;
	private String price;
	private String remarks;


	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<Item> items = new ArrayList<>();
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<ERPOrder> erpOrders = new ArrayList<>();
	
	public Order() {
		super();
	}

	public Order(String id, String customer, List<Item> items, String price, String remarks, List<ERPOrder> erpOrders) {
		super();
		this.id = id;
		this.customer = customer;
		this.items = items;
		this.price = price;
		this.remarks = remarks;
		this.erpOrders = erpOrders;
	}

	public List<ERPOrder> getErpOrders() {
		return erpOrders;
	}

	public void setErpOrders(List<ERPOrder> erpOrders) {
		this.erpOrders = erpOrders;
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
		String ord = "{" + "\"id\":\"" + id + "\"," + "\"customer\":\"" + customer + "\"," + "items{";

		for (int i = 0; i < items.size(); i++) {
			ord = ord + "item " + items.get(i).toString() + ", ";
		}

		ord = ord + "}," + "\"price\":\"" + price + "\"," + "\"remarks\":\"" + remarks + "\"" + "}";
		return ord;
	}

}

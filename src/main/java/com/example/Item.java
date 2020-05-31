package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "ORDER_ITEMS")
public class Item {
	
	@Id
    @GeneratedValue
    private Long id;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name ="order_id", nullable = false)
    private Order order;
	
	private String code;
	private String quantity;
	
	public Item() {
		super();
	}
	public Item(Long id, String code, String quantity, Order order) {
		super();
		this.code = code;
		this.quantity = quantity;
		this.id = id;
		this.order=order;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getQuantity() {
		return quantity;
	}
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	} 
	
	public String toString() {
		String serial = "{"
				+ "\"code\":\"" + code + "\","
				+ "\"quantity\":\"" + quantity + "\""
				+"}";
		return serial;
	}
	
}

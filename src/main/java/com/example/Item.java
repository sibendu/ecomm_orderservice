package com.example;

public class Item {
	
	private String code;
	private String quantity;
	
	public Item() {
		super();
	}
	public Item(String code, String quantity) {
		super();
		this.code = code;
		this.quantity = quantity;
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
				+ "\"quantity\":\"" + quantity + "\","
				+"}";
		return serial;
	}
	
}

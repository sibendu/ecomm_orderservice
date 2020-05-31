package com.example;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name = "ERP_ORDERS")
public class ERPOrder {
	
	@Id
    private String erpOrderId;
	
	private String erpName;
	
	@JsonIgnore
	@ManyToOne
    @JoinColumn(name ="order_id", nullable = false)
    private Order order;
	
	private String status;
	
	public ERPOrder() {
		super();
	}
	
	

	public ERPOrder(String erpOrderId, String erpName, Order order, String status) {
		super();
		this.erpOrderId = erpOrderId;
		this.erpName = erpName;
		this.order = order;
		this.status = status;
	}
	
	public String toString() {
		String serial = "{"
				+ "\"erpOrderId\":\"" + erpOrderId + "\","
				+ "\"erpName\":\"" + erpName + "\","
				+ "\"status\":\"" + status + "\""
				+"}";
		return serial;
	}



	public String getErpOrderId() {
		return erpOrderId;
	}



	public void setErpOrderId(String erpOrderId) {
		this.erpOrderId = erpOrderId;
	}



	public String getErpName() {
		return erpName;
	}



	public void setErpName(String erpName) {
		this.erpName = erpName;
	}



	public Order getOrder() {
		return order;
	}



	public void setOrder(Order order) {
		this.order = order;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}
	
}

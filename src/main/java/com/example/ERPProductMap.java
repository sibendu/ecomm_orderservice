package com.example;

import java.io.Serializable;
import java.util.List;

public class ERPProductMap implements Serializable {
	
	private List<Item> mp1Products;
	private List<Item> prdProducts;
	
	public ERPProductMap() {
		super();
	}
	
	
	public ERPProductMap(List<Item> mp1Products, List<Item> prdProducts) {
		super();
		this.mp1Products = mp1Products;
		this.prdProducts = prdProducts;
	}


	public List<Item> getMp1Products() {
		return mp1Products;
	}


	public void setMp1Products(List<Item> mp1Products) {
		this.mp1Products = mp1Products;
	}


	public List<Item> getPrdProducts() {
		return prdProducts;
	}


	public void setPrdProducts(List<Item> prdProducts) {
		this.prdProducts = prdProducts;
	}
	
}

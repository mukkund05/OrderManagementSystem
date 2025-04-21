 
package com.hexaware.entity;

public class Electronics extends Product{
	
	
	private String brand;
    private int warrantyPeriod;
	

    public Electronics(int productId, String productName, String description, double price, int quantityInStock, Type type, String brand, int warrantyPeriod) {
        super(productId, productName, description, price, quantityInStock, type);
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }
    
    public Electronics(String productName, String description, double price, int quantityInStock, Type type, String brand, int warrantyPeriod) {
        super(0, productName, description, price, quantityInStock, type); // 0 as placeholder
        this.brand = brand;
        this.warrantyPeriod = warrantyPeriod;
    }



	public String getBrand() {
		return brand;
	}


	public void setBrand(String brand) {
		this.brand = brand;
	}


	public int getWarrantyPeriod() {
		return warrantyPeriod;
	}


	public void setWarrantyPeriod(int warrantyPeriod) {
		this.warrantyPeriod = warrantyPeriod;
	}
    
    
    

	

}

package com.hexaware.entity;

public class Clothing extends Product {
    
    private String size;   
    private String color;  
    
    // Constructor
    public Clothing(int productId, String productName, String description, double price, int quantityInStock, Type type, String size, String color) {
        super(productId, productName, description, price, quantityInStock, type);
        this.size = size;
        this.color = color;
    }
    
    public Clothing(String productName, String description, double price, int quantityInStock, Type type, String size, String color) {
        super(0, productName, description, price, quantityInStock, type); 
        this.size = size;
        this.color = color;
    }


    // Getters and Setters
    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
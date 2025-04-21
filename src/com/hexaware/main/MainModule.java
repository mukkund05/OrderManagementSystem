package com.hexaware.main;

import com.hexaware.dao.OrderProcessor;
import com.hexaware.entity.*;
import java.util.*;

public class MainModule {
    static Scanner scanner = new Scanner(System.in);
    static OrderProcessor orderProcessor = new OrderProcessor();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n--- Order Management System ---");
            System.out.println("1. Create User");
            System.out.println("2. Create Product");
            System.out.println("3. Create Order");
            System.out.println("4. Cancel Order");
            System.out.println("5. Get All Products");
            System.out.println("6. Get Orders by User");
            System.out.println("7. Exit");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> createUser();
                    case 2 -> createProduct();
                    case 3 -> createOrder();
                    case 4 -> cancelOrder();
                    case 5 -> getAllProducts();
                    case 6 -> getOrderByUser();
                    case 7 -> {
                        System.out.println("Exiting...");
                        System.exit(0);
                    }
                    default -> System.out.println("Invalid choice. Please enter a number between 1 and 7.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void createUser() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        System.out.print("Role (Admin/User): ");
        String role = scanner.nextLine().trim();
        try {
            User.Role userRole = User.Role.valueOf(role);
            User user = new User(username, password, userRole);
            orderProcessor.createUser(user);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid role. Please enter 'Admin' or 'User'.");
        }
    }

    private static void createProduct() {
        System.out.print("Admin Username: ");
        String username = scanner.nextLine();
        System.out.print("Admin Password: ");
        String password = scanner.nextLine();
        User user = new User(username, password, User.Role.Admin);

        System.out.print("Product Name: ");
        String name = scanner.nextLine();
        System.out.print("Description: ");
        String desc = scanner.nextLine();
        System.out.print("Price: ");
        double price;
        try {
            price = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("Invalid price. Please enter a number.");
            scanner.nextLine();
            return;
        }
        System.out.print("Stock: ");
        int stock;
        try {
            stock = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid stock. Please enter an integer.");
            scanner.nextLine();
            return;
        }
        scanner.nextLine();
        System.out.print("Type (ELECTRONICS/CLOTHING): ");
        String type = scanner.nextLine().trim().toUpperCase();

        Product product = null;
        try {
            Product.Type productType = Product.Type.valueOf(type);
            if (productType == Product.Type.ELECTRONICS) {
                System.out.print("Brand: ");
                String brand = scanner.nextLine();
                System.out.print("Warranty (months): ");
                int warranty;
                try {
                    warranty = scanner.nextInt();
                } catch (InputMismatchException e) {
                    System.out.println("Invalid warranty. Please enter an integer.");
                    scanner.nextLine();
                    return;
                }
                scanner.nextLine();
                product = new Electronics(name, desc, price, stock, productType, brand, warranty);
            } else if (productType == Product.Type.CLOTHING) {
                System.out.print("Size: ");
                String size = scanner.nextLine();
                System.out.print("Color: ");
                String color = scanner.nextLine();
                product = new Clothing(name, desc, price, stock, productType, size, color);
            }
            orderProcessor.createProduct(user, product);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid product type. Please enter 'ELECTRONICS' or 'CLOTHING'.");
        }
    }

    private static void createOrder() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        User user = new User(username, password, User.Role.User);

        List<Product> allProducts = orderProcessor.getAllProducts();
        if (allProducts.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        System.out.println("Available Products:");
        for (Product p : allProducts) {
            System.out.println(p.getProductId() + ": " + p.getProductName());
        }

        System.out.print("Enter Product IDs (comma separated): ");
        String[] ids = scanner.nextLine().split(",");
        List<Product> orderList = new ArrayList<>();
        try {
            for (String id : ids) {
                int pid = Integer.parseInt(id.trim());
                Optional<Product> product = allProducts.stream().filter(p -> p.getProductId() == pid).findFirst();
                if (product.isPresent()) {
                    orderList.add(product.get());
                } else {
                    System.out.println("Product ID " + pid + " not found.");
                    return;
                }
            }
            orderProcessor.createOrder(user, orderList);
        } catch (NumberFormatException e) {
            System.out.println("Invalid product ID format. Please enter valid numbers.");
        }
    }

    private static void cancelOrder() {
        System.out.print("Enter your User ID: ");
        int userId;
        try {
            userId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid User ID. Please enter an integer.");
            scanner.nextLine();
            return;
        }
        System.out.print("Enter Order ID to cancel: ");
        int orderId;
        try {
            orderId = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println("Invalid Order ID. Please enter an integer.");
            scanner.nextLine();
            return;
        }
        scanner.nextLine();
        orderProcessor.cancelOrder(userId, orderId);
    }

    private static void getAllProducts() {
        List<Product> products = orderProcessor.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("No products found.");
        } else {
            for (Product p : products) {
                System.out.println(p.getProductId() + ": " + p.getProductName() + " - " + p.getType());
            }
        }
    }

    private static void getOrderByUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();
        User user = new User(username, password, User.Role.User);
        List<Product> orders = orderProcessor.getOrderByUser(user);
        if (orders.isEmpty()) {
            System.out.println("No orders found for user " + username);
        } else {
            for (Product p : orders) {
                System.out.println(p.getProductId() + ": " + p.getProductName());
            }
        }
    }
}
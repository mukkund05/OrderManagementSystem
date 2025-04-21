package com.hexaware.dao;

import com.hexaware.entity.*;
import com.hexaware.util.*;
import com.hexaware.exceptions.*;

import java.sql.*;
import java.util.*;

public class OrderProcessor implements IOrderManagementRepository {
    private Connection connection;

    public OrderProcessor() {
        String propertyFileName = "E:\\JAVA\\Order Management\\OrderManagementSystem\\src\\com\\hexaware\\util\\db.properties";
        this.connection = DBConnUtil.getConnection(propertyFileName);
    }

    @Override
    public void createOrder(User user, List<Product> products) {
        try (Connection conn = DBUtil.getDBConn()) {
            
            if (!userExists(user.getUsername(), conn)) {
                createUser(user);
            }

            
            int userId;
            try {
                userId = getUserIdByUsername(user.getUsername(), conn);
            } catch (UserNotFoundException e) {
                System.out.println("Error: Failed to retrieve user ID after creation: " + e.getMessage());
                return;
            }

            // Check if products exist
            for (Product product : products) {
                if (!productExists(product.getProductId(), conn)) {
                    throw new ProductNotFoundException("Product ID " + product.getProductId() + " not found.");
                }
            }

            // Insert order
            for (Product product : products) {
                String query = "INSERT INTO Orders(userId, productId) VALUES(?, ?)";
                try (PreparedStatement ptst = conn.prepareStatement(query)) {
                    ptst.setInt(1, userId);
                    ptst.setInt(2, product.getProductId());
                    ptst.executeUpdate();
                }
            }

            System.out.println("Order created successfully.");

        } catch (SQLException e) {
            System.out.println("Database error in creating order: " + e.getMessage());
        } catch (ProductNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void cancelOrder(int userId, int orderId) {
        try (Connection conn = DBUtil.getDBConn()) {
            // Check if user exists
            if (!userExists(userId, conn)) {
                throw new UserNotFoundException("User ID " + userId + " not found.");
            }

            // Check if order exists
            String checkQuery = "SELECT * FROM Orders WHERE orderId=? AND userId=?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, orderId);
                checkStmt.setInt(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    throw new OrderNotFoundException("Order ID " + orderId + " not found for User ID " + userId);
                }
            }

            // Delete order
            String deleteQuery = "DELETE FROM Orders WHERE orderId=?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                deleteStmt.setInt(1, orderId);
                deleteStmt.executeUpdate();
                System.out.println("Order cancelled successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Database error in cancelling order: " + e.getMessage());
        } catch (UserNotFoundException | OrderNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void createProduct(User user, Product product) {
        try (Connection conn = DBUtil.getDBConn()) {
            // Verify user exists and is Admin
            if (!userExists(user.getUsername(), conn)) {
                throw new UserNotFoundException("User " + user.getUsername() + " not found.");
            }
            String roleQuery = "SELECT role FROM Users WHERE username=?";
            try (PreparedStatement roleStmt = conn.prepareStatement(roleQuery)) {
                roleStmt.setString(1, user.getUsername());
                ResultSet rs = roleStmt.executeQuery();
                if (rs.next() && !rs.getString("role").equals("Admin")) {
                    throw new IllegalAccessException("Only Admin users can add products.");
                }
            }

            // Insert product
            String query = "INSERT INTO Products(productName, description, price, quantityInStock, type, brand, warrantyPeriod, size, color) VALUES(?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement ptst = conn.prepareStatement(query)) {
                ptst.setString(1, product.getProductName());
                ptst.setString(2, product.getDescription());
                ptst.setDouble(3, product.getPrice());
                ptst.setInt(4, product.getQuantityInStock());
                ptst.setString(5, product.getType().toString());

                if (product instanceof Electronics) {
                    ptst.setString(6, ((Electronics) product).getBrand());
                    ptst.setInt(7, ((Electronics) product).getWarrantyPeriod());
                    ptst.setNull(8, Types.VARCHAR);
                    ptst.setNull(9, Types.VARCHAR);
                } else if (product instanceof Clothing) {
                    ptst.setNull(6, Types.VARCHAR);
                    ptst.setNull(7, Types.INTEGER);
                    ptst.setString(8, ((Clothing) product).getSize());
                    ptst.setString(9, ((Clothing) product).getColor());
                } else {
                    ptst.setNull(6, Types.VARCHAR);
                    ptst.setNull(7, Types.INTEGER);
                    ptst.setNull(8, Types.VARCHAR);
                    ptst.setNull(9, Types.VARCHAR);
                }

                ptst.executeUpdate();
                System.out.println("Product created successfully!");
            }

        } catch (SQLException e) {
            System.out.println("Database error in creating product: " + e.getMessage());
        } catch (UserNotFoundException | IllegalAccessException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void createUser(User user) {
        try (Connection conn = DBUtil.getDBConn()) {
            if (userExists(user.getUsername(), conn)) {
                throw new IllegalArgumentException("User " + user.getUsername() + " already exists.");
            }

            String query = "INSERT INTO Users(username, password, role) VALUES(?,?,?)";
            try (PreparedStatement ptst = conn.prepareStatement(query)) {
                ptst.setString(1, user.getUsername());
                ptst.setString(2, user.getPassword());
                ptst.setString(3, user.getRole().toString());
                ptst.executeUpdate();
                System.out.println("User created successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Database error in creating user: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        String query = "SELECT * FROM Products";

        try (Connection conn = DBUtil.getDBConn(); PreparedStatement ptst = conn.prepareStatement(query)) {
            ResultSet rs = ptst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("productId");
                String name = rs.getString("productName");
                String desc = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("quantityInStock");
                Product.Type type = Product.Type.valueOf(rs.getString("type"));

                if (type == Product.Type.ELECTRONICS) {
                    String brand = rs.getString("brand");
                    int warranty = rs.getInt("warrantyPeriod");
                    productList.add(new Electronics(id, name, desc, price, stock, type, brand, warranty));
                } else if (type == Product.Type.CLOTHING) {
                    String size = rs.getString("size");
                    String color = rs.getString("color");
                    productList.add(new Clothing(id, name, desc, price, stock, type, size, color));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error in fetching products: " + e.getMessage());
        }

        return productList;
    }

    @Override
    public List<Product> getOrderByUser(User user) {
        List<Product> orderedProducts = new ArrayList<>();
        String query = "SELECT p.* FROM Products p JOIN Orders o ON p.productId = o.productId JOIN Users u ON o.userId = u.userId WHERE u.username = ?";

        try (Connection conn = DBUtil.getDBConn(); PreparedStatement ptst = conn.prepareStatement(query)) {
            ptst.setString(1, user.getUsername());
            ResultSet rs = ptst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("productId");
                String name = rs.getString("productName");
                String desc = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("quantityInStock");
                Product.Type type = Product.Type.valueOf(rs.getString("type"));

                if (type == Product.Type.ELECTRONICS) {
                    String brand = rs.getString("brand");
                    int warranty = rs.getInt("warrantyPeriod");
                    orderedProducts.add(new Electronics(id, name, desc, price, stock, type, brand, warranty));
                } else if (type == Product.Type.CLOTHING) {
                    String size = rs.getString("size");
                    String color = rs.getString("color");
                    orderedProducts.add(new Clothing(id, name, desc, price, stock, type, size, color));
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error in fetching orders: " + e.getMessage());
        }

        return orderedProducts;
    }

    // Utility Methods
    private boolean userExists(String username, Connection conn) throws SQLException {
        String query = "SELECT * FROM Users WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean userExists(int userId, Connection conn) throws SQLException {
        String query = "SELECT * FROM Users WHERE userId=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private int getUserIdByUsername(String username, Connection conn) throws SQLException, UserNotFoundException {
        String query = "SELECT userId FROM Users WHERE username=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("userId");
            } else {
                throw new UserNotFoundException("User " + username + " not found.");
            }
        }
    }

    private boolean productExists(int productId, Connection conn) throws SQLException {
        String query = "SELECT * FROM Products WHERE productId=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
}
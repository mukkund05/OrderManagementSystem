CREATE DATABASE IF NOT EXISTS ordermanagementsystem;
USE ordermanagementsystem;

CREATE TABLE Users (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('Admin', 'User') NOT NULL
);


CREATE TABLE Products (
    productId INT AUTO_INCREMENT PRIMARY KEY,
    productName VARCHAR(100) NOT NULL,
    description TEXT,
    price DOUBLE NOT NULL,
    quantityInStock INT NOT NULL,
    type ENUM('ELECTRONICS', 'CLOTHING') NOT NULL,
    brand VARCHAR(50),
    warrantyPeriod INT,
    size VARCHAR(20),
    color VARCHAR(20)
);


CREATE TABLE Orders (
    orderId INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    productId INT NOT NULL,
    FOREIGN KEY (userId) REFERENCES Users(userId),
    FOREIGN KEY (productId) REFERENCES Products(productId)
);




select * from products;
select * from USers;
select * from Orders;





CREATE TABLE users (
                       user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP
);



CREATE TABLE vendors (
                         vendor_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         user_id BIGINT NOT NULL UNIQUE,
                         company_name VARCHAR(100),
                         contact_info VARCHAR(255),
                         created_at TIMESTAMP,
                         updated_at TIMESTAMP,
                         FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE categories (
                            category_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(50) NOT NULL,
                            description VARCHAR(255)
);

CREATE TABLE products (
                          product_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          vendor_id BIGINT NOT NULL,
                          category_id BIGINT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          description VARCHAR(255),
                          base_price DECIMAL(10,2) NOT NULL,
                          image_url VARCHAR(255),
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id),
                          FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

CREATE TABLE auction_slots (
                               slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               product_id BIGINT NOT NULL UNIQUE,
                               start_time TIMESTAMP NOT NULL,
                               end_time TIMESTAMP NOT NULL,
                               status VARCHAR(20) NOT NULL,
                               created_at TIMESTAMP,
                               updated_at TIMESTAMP,
                               FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE bids (
                      bid_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      slot_id BIGINT NOT NULL,
                      user_id BIGINT NOT NULL,
                      bid_amount DECIMAL(10,2) NOT NULL,
                      bid_time TIMESTAMP NOT NULL,
                      created_at TIMESTAMP,
                      updated_at TIMESTAMP,
                      FOREIGN KEY (slot_id) REFERENCES auction_slots(slot_id),
                      FOREIGN KEY (user_id) REFERENCES users(user_id)
);


-- inserting some initial data

-- Insert Users
INSERT INTO users (username, email, password, created_at) VALUES
                                                              ('john_doe', 'john@example.com', 'password123', CURRENT_TIMESTAMP),
                                                              ('jane_smith', 'jane@example.com', 'password456', CURRENT_TIMESTAMP);

-- Insert Vendors
INSERT INTO vendors (user_id, company_name, contact_info, created_at) VALUES
    (1, 'JohnElectronics', '123-456-7890', CURRENT_TIMESTAMP),
(2, 'Janes Books', '987-654-3210', CURRENT_TIMESTAMP);

-- Insert Categories
INSERT INTO categories (name, description) VALUES
                                               ('Electronics', 'Electronic gadgets and devices'),
                                               ('Books', 'Various kinds of books');

-- Insert Products
INSERT INTO products (vendor_id, category_id, name, description, base_price, created_at) VALUES
                                                                                             (1, 1, 'Smartphone', 'Latest model smartphone', 500.00, CURRENT_TIMESTAMP),
                                                                                             (2, 2, 'Novel', 'Bestselling novel', 15.00, CURRENT_TIMESTAMP);

-- Insert Auction Slots
INSERT INTO auction_slots (product_id, start_time, end_time, status, created_at) VALUES
                                                                                     (1, CURRENT_TIMESTAMP, DATEADD('HOUR', 2, CURRENT_TIMESTAMP), 'ACTIVE', CURRENT_TIMESTAMP),
                                                                                     (2, CURRENT_TIMESTAMP, DATEADD('DAY', 1, CURRENT_TIMESTAMP), 'SCHEDULED', CURRENT_TIMESTAMP);

-- Insert Bids
INSERT INTO bids (slot_id, user_id, bid_amount, bid_time, created_at) VALUES
    (1, 2, 550.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

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



////NEW QUERIES
-- 1. Create users table
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for users
CREATE INDEX idx_username ON users(username);
CREATE INDEX idx_email ON users(email);

-- 2. Create authentication table
CREATE TABLE authentication (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create index for authentication
CREATE INDEX idx_auth_username ON authentication(username);

-- 3. Create categories table
CREATE TABLE categories (
    category_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT
);

-- Create index for categories
CREATE INDEX idx_category_name ON categories(name);

-- 4. Create vendors table
CREATE TABLE vendors (
    vendor_id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    company_name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create index for vendors
CREATE INDEX idx_vendor_user_id ON vendors(user_id);

-- 5. Create products table
CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    vendor_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    base_price DECIMAL(19,4),
    image_url VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (vendor_id) REFERENCES vendors(vendor_id),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Create indexes for products
CREATE INDEX idx_product_vendor_id ON products(vendor_id);
CREATE INDEX idx_product_category_id ON products(category_id);
CREATE INDEX idx_product_name ON products(name);

-- 6. Create auction_slots table with column-based partitioning
CREATE TABLE auction_slots (
    slot_id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    start_time TIMESTAMP NOT NULL,
    start_year INT GENERATED ALWAYS AS (EXTRACT(YEAR FROM start_time)) STORED,
    end_time TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (product_id) REFERENCES products(product_id)
)
-- Index for product_id to improve lookups by product
CREATE INDEX idx_product_id ON public.auction_slots USING btree (product_id);

-- Index for status to allow efficient querying by auction status
CREATE INDEX idx_status ON public.auction_slots USING btree (status);

-- Index for start_time to optimize range queries based on start time
CREATE INDEX idx_start_time ON public.auction_slots USING btree (start_time);

-- 7. Create bids table
CREATE TABLE bids (
    bid_id UUID PRIMARY KEY,
    slot_id UUID NOT NULL,
    user_id UUID NOT NULL,
    bid_amount DECIMAL(19,4) NOT NULL,
    bid_time TIMESTAMP NOT NULL,
    FOREIGN KEY (slot_id) REFERENCES auction_slots(slot_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create indexes for bids
CREATE INDEX idx_bid_slot_id ON bids(slot_id);
CREATE INDEX idx_bid_user_id ON bids(user_id);
CREATE INDEX idx_bid_bid_amount ON bids(bid_amount);

-- 8. Create notification_preferences table
CREATE TABLE notification_preferences (
    preference_id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    channel VARCHAR(50) NOT NULL,
    message_type VARCHAR(50) NOT NULL,
    subscribed BOOLEAN NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE (user_id, channel, message_type)
);

-- Create index for notification_preferences
CREATE INDEX idx_notification_preference_user_id ON notification_preferences(user_id);

-- 9. Create winners table
CREATE TABLE winners (
    winner_id UUID PRIMARY KEY,
    slot_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    bid_id UUID NOT NULL UNIQUE,
    notified BOOLEAN NOT NULL DEFAULT FALSE,
    notification_time TIMESTAMP,
    FOREIGN KEY (slot_id) REFERENCES auction_slots(slot_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (bid_id) REFERENCES bids(bid_id)
);

-- Create indexes for winners
CREATE INDEX idx_winner_slot_id ON winners(slot_id);
CREATE INDEX idx_winner_user_id ON winners(user_id);
CREATE INDEX idx_winner_bid_id ON winners(bid_id);

-- 10. Create notifications table
CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    message TEXT NOT NULL,
    sent_time TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Create indexes for notifications
CREATE INDEX idx_notification_user_id ON notifications(user_id);
CREATE INDEX idx_notification_sent_time ON notifications(sent_time);

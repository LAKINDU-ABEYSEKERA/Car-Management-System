-- V1__init_schema.sql

CREATE TABLE user (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    role VARCHAR(50)
);

CREATE TABLE car (
    car_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    brand VARCHAR(255),
    model VARCHAR(255),
    seating_capacity INT,
    fuel_type VARCHAR(50),
    price_per_day DOUBLE,
    status VARCHAR(50),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    version BIGINT,
    created_by VARCHAR(255),
        created_date DATETIME,
        last_modified_by VARCHAR(255),
        last_modified_date DATETIME
);

CREATE TABLE customer (
    customer_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(255),
    address VARCHAR(255),
    email VARCHAR(255),
    created_by VARCHAR(255),
        created_date DATETIME,
        last_modified_by VARCHAR(255),
        last_modified_date DATETIME
);

CREATE TABLE driver (
    driver_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    licence_no VARCHAR(255),
    status VARCHAR(50)
);

CREATE TABLE booking (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    start_date DATE,
    end_date DATE,
    total_price DOUBLE,
    with_driver BOOLEAN,
    booking_status VARCHAR(50),
    car_id BIGINT,
    customer_id BIGINT,
    driver_id BIGINT,
    user_id BIGINT,
    created_by VARCHAR(255),
        created_date DATETIME,
        last_modified_by VARCHAR(255),
        last_modified_date DATETIME,
    CONSTRAINT fk_booking_car FOREIGN KEY (car_id) REFERENCES car(car_id),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT fk_booking_driver FOREIGN KEY (driver_id) REFERENCES driver(driver_id),
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- THE NEW ENTERPRISE PAYMENT TABLE (Auth & Capture)
CREATE TABLE payment (
    payment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_method VARCHAR(50),
    auth_date DATETIME,
    capture_date DATETIME,
    estimated_amount DOUBLE,
    security_deposit DOUBLE,
    final_captured_amount DOUBLE,
    payment_status VARCHAR(50),
    booking_id BIGINT UNIQUE,
    created_by VARCHAR(255),
        created_date DATETIME,
        last_modified_by VARCHAR(255),
        last_modified_date DATETIME,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id)
);
USE mishra_travels;

-- 1. Foreign Key Checks temporarily band karein taaki drop karne mein dikkat na ho
SET FOREIGN_KEY_CHECKS = 0;

-- 2. Agar koi table pehle se bani hai toh use uda dein
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS admins;

-- 3. Naye sire se fresh tables banayein
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mobile VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE admins (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price_per_day DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    fuel_type VARCHAR(50) NOT NULL,
    features TEXT,
    description TEXT,
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT TRUE
);

CREATE TABLE bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(50) NOT NULL UNIQUE,
    user_id BIGINT,
    car_id BIGINT,
    customer_name VARCHAR(100) NOT NULL,
    customer_mobile VARCHAR(15) NOT NULL,
    customer_email VARCHAR(100) NOT NULL,
    pickup_location VARCHAR(255) NOT NULL,
    drop_location VARCHAR(255) NOT NULL,
    pickup_date DATE NOT NULL,
    return_date DATE NOT NULL,
    special_requirements TEXT,
    advance_amount DECIMAL(10,2) DEFAULT 100.00,
    status VARCHAR(50) DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE SET NULL
);

CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    transaction_id VARCHAR(100) NOT NULL UNIQUE,
    status VARCHAR(50) DEFAULT 'Success',
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id) ON DELETE CASCADE
);

-- 4. Default Dummy Data insert karein
INSERT INTO admins (email, password) VALUES 
('admin@mishratravels.com', '$2a$10$pL1m.PqW.pZ9Lg1Kpx.C4ep3XUvUfA1zFkIuQv9aE3zTz8yGjY2Xq');

INSERT INTO cars (name, price_per_day, capacity, fuel_type, features, description, image_url, is_available) VALUES
('Innova Crysta', 3000.00, 7, 'Diesel', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Leather Seats', 'The premium MUV from Toyota, perfect for family road trips and group travel in absolute comfort.', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?q=80&w=600&auto=format&fit=crop', TRUE),
('Thar', 4000.00, 4, 'Diesel', 'Air Conditioning,Music System,GPS Navigation,Power Steering,4x4 Drive,Convertible Roof', 'Best SUV for adventure trips, off-road travels, and making a bold style statement on any terrain.', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?q=80&w=600&auto=format&fit=crop', TRUE),
('Fortuner', 6000.00, 7, 'Diesel', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Sunroof,Ventilated Seats', 'A powerful SUV combining rugged capabilities with premium luxury and commanding road presence.', 'https://images.unsplash.com/photo-1617469767053-d3b508a0d84d?q=80&w=600&auto=format&fit=crop', TRUE),
('Scorpio Classic', 3200.00, 7, 'Diesel', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Rear AC Vents', 'An iconic Indian SUV offering raw power, high seating position, and great long-distance comfort.', 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?q=80&w=600&auto=format&fit=crop', TRUE),
('Ertiga', 2500.00, 7, 'Petrol/CNG', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Dual Airbags', 'Smart and economical 7-seater MUV, ideal for city travels, airport runs, and budget-friendly trips.', 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?q=80&w=600&auto=format&fit=crop', TRUE),
('Verna', 2800.00, 5, 'Petrol', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Sunroof,Cruise Control', 'Sleek and sporty sedan with modern technology, smooth driving dynamics, and premium cabin space.', 'https://images.unsplash.com/photo-1549399542-7e3f8b79c341?q=80&w=600&auto=format&fit=crop', TRUE),
('Lamborghini Huracan', 45000.00, 2, 'Petrol', 'Air Conditioning,Music System,GPS Navigation,Power Steering,Sports Mode,Leather Trim', 'Experience ultimate luxury and raw supercar performance. Perfect for special entries and premium shoots.', 'https://images.unsplash.com/photo-1525609004556-c46c7d6cf0a3?q=80&w=600&auto=format&fit=crop', TRUE);

-- 5. Foreign Key Checks wapas active karein
SET FOREIGN_KEY_CHECKS = 1;

-- Tables list check karein
SHOW TABLES;
select * from users;
select * from cars;
select * from bookings;
USE mishra_travels;
USE mishra_travels;
select * from admins;
select * from payments;
-- 1. Fortuner ki photo ID = 3 se update karein
USE mishra_travels;
UPDATE cars 
SET image_url = 'https://carfinderassets.s3.ap-south-1.amazonaws.com/carFinder/cars/1774342526151-car-thumbnail.webp' 
WHERE id = 3;
-- 2. Lamborghini ki photo ID = 7 se update karein
UPDATE cars 
SET image_url = 'https://images.unsplash.com/photo-1614162692292-7ac56d7f7f1e?q=80&w=600&auto=format&fit=crop' 
WHERE id = 7;
-- =======================================================
-- ID 2 MEIN ASLI MAHINDRA THAR KA IMAGE URL UPDATE KARNA
-- =======================================================
USE mishra_travels;

-- =======================================================
-- ID 2 MEIN EKADAM ASLI BLACK MAHINDRA THAR KA URL SET KARNA
-- =======================================================
SET SQL_SAFE_UPDATES = 0;
UPDATE cars 
SET image_url = 'https://encrypted-tbn0.gstatic.com/licensed-image?q=tbn:ANd9GcRV8D2rwymexX1MzEzHUMLeh5PIU8oUukG2WbK8rC_UF0fU5K9gHRIWjXKseLXE7sRrV3-hv5tCKsZ0SLI' 
WHERE id = 2;

SET SQL_SAFE_UPDATES = 1;
USE mishra_travels;

-- Safe updates ko off karte hain taaki query bina kisi issue ke chal jaye
SET SQL_SAFE_UPDATES = 0;

USE mishra_travels;

-- Safe updates off taaki bina kisi warning ke chal jaye
SET SQL_SAFE_UPDATES = 0;

-- =======================================================
-- ID 5 MEIN REAL MARUTI ERTIGA KA STABLE URL
-- =======================================================
UPDATE cars 
SET image_url = 'https://images.unsplash.com/photo-1663148152579-bd323068e1d5?q=80&w=600&auto=format&fit=crop' 
WHERE id = 5;

-- =======================================================
-- ID 6 MEIN REAL HYUNDAI VERNA KA STABLE URL
-- =======================================================
UPDATE cars 
SET image_url = 'https://images.unsplash.com/photo-1619767886558-efdc259cde1a?q=80&w=600&auto=format&fit=crop' 
WHERE id = 6;

-- Safe updates wapas on
SET SQL_SAFE_UPDATES = 1;
select * from users;
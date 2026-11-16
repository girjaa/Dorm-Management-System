-- ========================================================
-- Dormitory Management System Database Schema
-- Database: dorm_db
-- ========================================================

CREATE DATABASE IF NOT EXISTS dorm_db;
USE dorm_db;

-- 1. Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    room_id VARCHAR(50) PRIMARY KEY,
    room_number INT NOT NULL,
    floor INT NOT NULL,
    building VARCHAR(100) NOT NULL,
    capacity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_capacity CHECK (capacity > 0),
    CONSTRAINT chk_room_number CHECK (room_number > 0)
);

-- 2. Students Table
CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    room_id VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE SET NULL
);

-- 3. Anonymous Complaints Table
-- Notice: NO reference to student_id to ensure anonymity!
CREATE TABLE IF NOT EXISTS complaints (
    complaint_id VARCHAR(50) PRIMARY KEY,
    description TEXT NOT NULL,
    category ENUM('PLUMBING', 'ELECTRICAL', 'STRUCTURAL', 'PEST_CONTROL', 'NOISE', 'HEATING_COOLING', 'CLEANING', 'OTHER') NOT NULL,
    status ENUM('OPEN', 'IN_PROGRESS', 'ASSIGNED', 'RESOLVED', 'CLOSED') DEFAULT 'OPEN',
    priority ENUM('LOW', 'NORMAL', 'HIGH', 'URGENT') DEFAULT 'NORMAL',
    room_id VARCHAR(50) NOT NULL,
    assigned_to VARCHAR(150) DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- 4. Administrators Table
CREATE TABLE IF NOT EXISTS administrators (
    admin_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL
);

-- 5. Maintenance Staff Table
CREATE TABLE IF NOT EXISTS maintenance_staff (
    staff_id VARCHAR(50) PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

-- 6. Announcements Table
CREATE TABLE IF NOT EXISTS announcements (
    announcement_id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    message TEXT NOT NULL,
    posted_by VARCHAR(150) NOT NULL,
    target_building VARCHAR(100) DEFAULT NULL,
    posted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. Forum Posts Table
CREATE TABLE IF NOT EXISTS forum_posts (
    post_id VARCHAR(50) PRIMARY KEY,
    title VARCHAR(250) NOT NULL,
    content TEXT NOT NULL,
    author_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 8. Forum Replies Table
CREATE TABLE IF NOT EXISTS forum_replies (
    reply_id VARCHAR(50) PRIMARY KEY,
    post_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    author_name VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (post_id) REFERENCES forum_posts(post_id) ON DELETE CASCADE
);

-- 9. Room Inspections Table
CREATE TABLE IF NOT EXISTS room_inspections (
    inspection_id VARCHAR(50) PRIMARY KEY,
    room_id VARCHAR(50) NOT NULL,
    scheduled_date DATETIME NOT NULL,
    completed_date DATETIME DEFAULT NULL,
    inspector_name VARCHAR(150) NOT NULL,
    notes TEXT,
    status ENUM('SCHEDULED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') DEFAULT 'SCHEDULED',
    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE
);

-- ========================================================
-- Initial Seed Data
-- ========================================================

INSERT IGNORE INTO rooms (room_id, room_number, floor, building, capacity) VALUES
('Building A-1-101', 101, 1, 'Building A', 2),
('Building A-1-102', 102, 1, 'Building A', 2),
('Building A-2-201', 201, 2, 'Building A', 3),
('Building B-1-101', 101, 1, 'Building B', 2);

INSERT IGNORE INTO administrators (admin_id, name, email, department) VALUES
('ADM01', 'Dorm Manager Office', 'manager@dorm.university.ro', 'Campus Facilities');

INSERT IGNORE INTO maintenance_staff (staff_id, name, specialization, available) VALUES
('STF01', 'Ion Popescu', 'Plumbing', TRUE),
('STF02', 'Vasile Ionescu', 'Electrical', TRUE),
('STF03', 'Mihai Radu', 'General Maintenance', TRUE);

INSERT IGNORE INTO announcements (announcement_id, title, message, posted_by, target_building) VALUES
('ANN01', 'Heating Maintenance Notice', 'Radiators will be tested on Tuesday between 10:00 and 14:00.', 'Dorm Manager Office', 'Building A');

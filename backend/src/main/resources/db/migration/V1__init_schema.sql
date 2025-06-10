-- Create community table
CREATE TABLE community (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    description TEXT,
    created_at DATETIME
);

-- Create owner table
CREATE TABLE owner (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(50),
    id_card VARCHAR(18),
    password_hash VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    status VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME
);

-- Create admin_user table
CREATE TABLE admin_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    role ENUM('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR') NOT NULL,
    community_id BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_at DATETIME,
    updated_at DATETIME,
    last_login_at DATETIME,
    FOREIGN KEY (community_id) REFERENCES community(id)
);

-- Create house table
CREATE TABLE house (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id BIGINT,
    community_id BIGINT NOT NULL,
    building VARCHAR(50),
    unit VARCHAR(50),
    room VARCHAR(50),
    address VARCHAR(255),
    area DECIMAL(10,2),
    certificate_number VARCHAR(100),
    is_primary BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (owner_id) REFERENCES owner(id),
    FOREIGN KEY (community_id) REFERENCES community(id)
);

-- Create vote_topic table
CREATE TABLE vote_topic (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    community_id BIGINT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME,
    end_time DATETIME,
    is_area_weighted BOOLEAN DEFAULT FALSE,
    is_real_name BOOLEAN DEFAULT TRUE,
    is_result_public BOOLEAN DEFAULT TRUE,
    status VARCHAR(20),
    created_by VARCHAR(50),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (community_id) REFERENCES community(id)
);

-- Create vote_option table
CREATE TABLE vote_option (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    option_text VARCHAR(255) NOT NULL,
    order_num INT,
    FOREIGN KEY (topic_id) REFERENCES vote_topic(id) ON DELETE CASCADE
);

-- Create vote_record table
CREATE TABLE vote_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    topic_id BIGINT NOT NULL,
    option_id BIGINT NOT NULL,
    house_id BIGINT NOT NULL,
    voter_id BIGINT NOT NULL,
    vote_weight DECIMAL(10,2) DEFAULT 1.00,
    vote_time DATETIME,
    ip_address VARCHAR(50),
    device_info VARCHAR(255),
    FOREIGN KEY (topic_id) REFERENCES vote_topic(id),
    FOREIGN KEY (option_id) REFERENCES vote_option(id),
    FOREIGN KEY (house_id) REFERENCES house(id),
    FOREIGN KEY (voter_id) REFERENCES owner(id)
);

-- Create announcement table
CREATE TABLE announcement (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    community_id BIGINT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    type ENUM('NOTICE', 'VOTE_RESULT', 'FINANCIAL', 'OTHER') NOT NULL,
    publisher_id BIGINT,
    publish_time DATETIME,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (community_id) REFERENCES community(id),
    FOREIGN KEY (publisher_id) REFERENCES admin_user(id)
);

-- Create announcement_attachment table
CREATE TABLE announcement_attachment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    announcement_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    upload_time DATETIME,
    FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE
);

-- Create announcement_read table
CREATE TABLE announcement_read (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    announcement_id BIGINT NOT NULL,
    owner_id BIGINT NOT NULL,
    read_time DATETIME,
    FOREIGN KEY (announcement_id) REFERENCES announcement(id) ON DELETE CASCADE,
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- Create suggestion table
CREATE TABLE suggestion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    community_id BIGINT,
    owner_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(20),
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (community_id) REFERENCES community(id),
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- Create suggestion_reply table
CREATE TABLE suggestion_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    suggestion_id BIGINT NOT NULL,
    replier_id BIGINT NOT NULL,
    replier_type VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    reply_time DATETIME,
    FOREIGN KEY (suggestion_id) REFERENCES suggestion(id) ON DELETE CASCADE
);

-- Create file_upload table
CREATE TABLE file_upload (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uploader_id BIGINT,
    uploader_type VARCHAR(20),
    original_name VARCHAR(255) NOT NULL,
    stored_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    file_size BIGINT,
    file_type VARCHAR(50),
    type ENUM('ID_CARD', 'HOUSE_CERT', 'OTHER'),
    upload_time DATETIME,
    FOREIGN KEY (uploader_id) REFERENCES owner(id) ON DELETE SET NULL
);

-- Create review_log table
CREATE TABLE review_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reviewer_id BIGINT,
    owner_id BIGINT,
    review_type VARCHAR(50) NOT NULL,
    status ENUM('APPROVED', 'REJECTED') NOT NULL,
    comment TEXT,
    review_time DATETIME,
    FOREIGN KEY (reviewer_id) REFERENCES admin_user(id),
    FOREIGN KEY (owner_id) REFERENCES owner(id)
);

-- Create system_log table
CREATE TABLE system_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    user_type ENUM('ADMIN', 'OWNER'),
    operation VARCHAR(50) NOT NULL,
    content TEXT,
    ip_address VARCHAR(50),
    created_time DATETIME
);

-- Add community_id foreign key to owner table
ALTER TABLE owner 
ADD COLUMN community_id BIGINT,
ADD CONSTRAINT fk_owner_community FOREIGN KEY (community_id) REFERENCES community(id); 
CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_at DATETIME2 NOT NULL
);

CREATE TABLE products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    category_id INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    link VARCHAR(255),
    image_url VARCHAR(255) NOT NULL,
    created_at DATETIME2 NOT NULL,
    updated_at DATETIME2 NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
); 
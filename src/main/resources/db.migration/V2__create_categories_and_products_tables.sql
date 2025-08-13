CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    category_id INTEGER NOT NULL,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(1000),
    link VARCHAR(255),
    image_url VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id)
);
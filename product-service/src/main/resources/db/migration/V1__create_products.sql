CREATE TABLE products (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    image_url VARCHAR(500),
    price NUMERIC(15, 2) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

INSERT INTO products (name, description, image_url, price) VALUES
    ('Vitamin C 1000mg', 'Vien uong bo sung Vitamin C', 'https://cdn.example.com/products/vitamin-c.jpg', 250000.00),
    ('Flash Sale Headphone', 'Tai nghe khong day chong on', 'https://cdn.example.com/products/headphone.jpg', 1990000.00),
    ('Omega 3 Fish Oil', 'Dau ca Omega 3 hop 100 vien', 'https://cdn.example.com/products/omega3.jpg', 480000.00);

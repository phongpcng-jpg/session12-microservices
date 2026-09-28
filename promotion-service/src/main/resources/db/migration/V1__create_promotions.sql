CREATE TABLE promotions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id BIGINT NOT NULL UNIQUE,
    promotion_price NUMERIC(15, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now()
);

INSERT INTO promotions (product_id, promotion_price, active) VALUES (2, 1490000.00, TRUE);

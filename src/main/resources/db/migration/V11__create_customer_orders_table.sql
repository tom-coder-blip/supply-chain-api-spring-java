-- Create customer_orders table
CREATE TABLE customer_orders (
                                 id BIGSERIAL PRIMARY KEY,
                                 customer_id BIGINT NOT NULL,
                                 product_id BIGINT NOT NULL,
                                 quantity INTEGER NOT NULL,
                                 status VARCHAR(50),
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 shipment_details TEXT,

                                 CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES users(id),
                                 CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES products(id)
);

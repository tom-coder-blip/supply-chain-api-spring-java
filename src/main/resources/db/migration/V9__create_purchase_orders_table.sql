CREATE TABLE purchase_orders (
                                 id BIGSERIAL PRIMARY KEY,
                                 warehouse_id BIGINT NOT NULL REFERENCES warehouses(id),
                                 supplier_id BIGINT NOT NULL REFERENCES users(id),
                                 product_id BIGINT NOT NULL REFERENCES products(id),
                                 quantity INTEGER NOT NULL,
                                 status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                                 created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 shipment_details JSONB
);

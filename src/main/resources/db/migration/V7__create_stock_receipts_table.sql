-- Create stock_receipts table
CREATE TABLE stock_receipts (
                                id BIGSERIAL PRIMARY KEY,

                                product_id BIGINT NOT NULL,
                                warehouse_id BIGINT NOT NULL,
                                quantity_received INT NOT NULL,
                                actor_id BIGINT NOT NULL,
                                timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                metadata JSONB,

                                CONSTRAINT fk_receipts_product
                                    FOREIGN KEY (product_id)
                                        REFERENCES products(id)
                                        ON DELETE RESTRICT
                                        ON UPDATE CASCADE,

                                CONSTRAINT fk_receipts_warehouse
                                    FOREIGN KEY (warehouse_id)
                                        REFERENCES warehouses(id)
                                        ON DELETE RESTRICT
                                        ON UPDATE CASCADE
);

-- Create warehouses table
CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            location VARCHAR(255),
                            manager_id BIGINT NOT NULL,

                            CONSTRAINT fk_warehouse_manager
                                FOREIGN KEY (manager_id)
                                    REFERENCES users(id)
                                    ON DELETE RESTRICT
                                    ON UPDATE CASCADE
);

-- Create inventory_items table
CREATE TABLE inventory_items (
                                 id BIGSERIAL PRIMARY KEY,
                                 product_id BIGINT NOT NULL,
                                 warehouse_id BIGINT NOT NULL,
                                 quantity INT NOT NULL,

                                 CONSTRAINT fk_inventory_product
                                     FOREIGN KEY (product_id)
                                         REFERENCES products(id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE,

                                 CONSTRAINT fk_inventory_warehouse
                                     FOREIGN KEY (warehouse_id)
                                         REFERENCES warehouses(id)
                                         ON DELETE CASCADE
                                         ON UPDATE CASCADE
);

CREATE TABLE stock_updates (
                               id BIGSERIAL PRIMARY KEY,

                               product_id BIGINT NOT NULL,
                               old_quantity INT NOT NULL,
                               new_quantity INT NOT NULL,
                               actor_id BIGINT NOT NULL,
                               timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               metadata JSONB,

                               CONSTRAINT fk_stock_updates_product
                                   FOREIGN KEY (product_id)
                                       REFERENCES products(id)
                                       ON DELETE CASCADE
                                       ON UPDATE CASCADE
);

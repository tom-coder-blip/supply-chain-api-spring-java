CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,

                          sku VARCHAR(50) NOT NULL UNIQUE,
                          name VARCHAR(100) NOT NULL,
                          unit VARCHAR(50),
                          quantity INT NOT NULL,

                          supplier_id BIGINT NOT NULL,

                          CONSTRAINT fk_products_supplier
                              FOREIGN KEY (supplier_id)
                                  REFERENCES users(id)
                                  ON DELETE RESTRICT
                                  ON UPDATE CASCADE
);

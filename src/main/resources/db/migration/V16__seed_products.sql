-- V16__seed_products.sql

-- Articulated Dump Truck
INSERT INTO products (id, sku, name, description, unit, price, quantity, category, archived, supplier_id)
VALUES (41, 'BEL-ADT-001', 'Articulated Dump Truck', 'Heavy-duty truck for transporting bulk materials', 'unit', 1850000.00, 5, 'Earthmoving Equipment', false, 21);

-- Front-End Loader
INSERT INTO products (id, sku, name, description, unit, price, quantity, category, archived, supplier_id)
VALUES (42, 'BEL-FEL-002', 'Front-End Loader', 'Used for loading materials into trucks or stockpiles', 'unit', 950000.00, 8, 'Loaders', false, 21);

-- Hydraulic Excavator
INSERT INTO products (id, sku, name, description, unit, price, quantity, category, archived, supplier_id)
VALUES (43, 'BEL-HEX-003', 'Hydraulic Excavator', 'Excavation and trenching for construction sites', 'unit', 1200000.00, 6, 'Excavators', false, 21);

-- Soil Compactor
INSERT INTO products (id, sku, name, description, unit, price, quantity, category, archived, supplier_id)
VALUES (44, 'BEL-CMP-004', 'Soil Compactor', 'Compacts soil for roadworks and foundations', 'unit', 670000.00, 10, 'Compaction Equipment', false, 21);

-- Mobile Compressor
INSERT INTO products (id, sku, name, description, unit, price, quantity, category, archived, supplier_id)
VALUES (45, 'BEL-CMP-005', 'Mobile Compressor', 'Portable air compressor for construction tools', 'unit', 320000.00, 12, 'Power Tools', false, 21);

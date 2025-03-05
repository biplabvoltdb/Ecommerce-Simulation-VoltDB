file -inlinebatch END_OF_BATCH
-- Warehouse Products Table
CREATE TABLE warehouse_products (
  product_id VARCHAR(36) NOT NULL,
  product_name VARCHAR(50) NOT NULL,
  stock BIGINT NOT NULL,
  price FLOAT NOT NULL,
  PRIMARY KEY (product_id)
);

-- Warehouse Wallets (Partitioned by product_id)
CREATE TABLE warehouse_wallets (
  product_id VARCHAR(36) NOT NULL,
  balance FLOAT NOT NULL,
  total_credited FLOAT NOT NULL,
  PRIMARY KEY (product_id)
);

-- Warehouse Orders Table (Partitioned by product_id)
CREATE TABLE warehouse_orders (
  order_id VARCHAR(36) NOT NULL,
  product_id VARCHAR(36) NOT NULL,
  amount_paid FLOAT NOT NULL,
  order_time TIMESTAMP NOT NULL,
  PRIMARY KEY (product_id, order_id)
);

-- Partitioning for tables
PARTITION TABLE warehouse_products ON COLUMN product_id;
PARTITION TABLE warehouse_wallets ON COLUMN product_id;
PARTITION TABLE warehouse_orders ON COLUMN product_id;


END_OF_BATCH
load classes /home/voltdb/coindcx/storedprocs.jar;
CREATE PROCEDURE PARTITION ON TABLE WAREHOUSE_PRODUCTS COLUMN PRODUCT_ID PARAMETER 1 FROM CLASS PlaceOrderProcedure;

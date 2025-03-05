# Ecommerce-Simulation-VoltDB

This repository contains a **demo** that simulates an **Order Management Transaction System** using **Volt Active Data**. It demonstrates **high-concurrency, ACID-compliant transactions** leveraging VoltDB's **partitioning and in-memory processing**.

## **Project Overview**
This demo is structured into three main components:

### **1. DDL.SQL (Schema Definition)**
- Defines the necessary **database tables** required for the simulation.
- Includes a **partitioned stored procedure definition** to optimize transaction processing.

### **2. Java Stored Procedure (PlaceOrderProcedure)**
The **`PlaceOrderProcedure`** is a **VoltDB stored procedure** responsible for handling order transactions atomically. The procedure performs the following steps **in a single transaction**:
1. **Checks stock availability** before processing an order.
2. **Reduces stock count** if the order is successful.
3. **Credits the respective product’s wallet** with the order amount.
4. **Inserts an order record** with a timestamp.
5. **Ensures atomic execution** of all these steps to maintain **consistency and integrity**.

### **3. Java Client Code**
A Java-based client that interacts with VoltDB to simulate **real-time order processing** at scale. This client:
- **Connects to VoltDB** to execute transactions.
- **Initializes warehouse stock** with predefined product data.
- **Simulates high-concurrency order placements** using multiple threads.

---

## **How the E-commerce Simulation Works**
This simulation replicates a **warehouse-based e-commerce system** where users place orders, and stock updates happen in **real time**. 

### **Workflow**
1. **Initialize VoltDB Client & Product List**
   - Sets up the connection and loads initial warehouse data.
   
2. **Connect to VoltDB Cluster**
   - Establishes connections to multiple VoltDB nodes.

3. **Initialize Warehouse Database**
   - Loads product inventory and initializes account balances.

4. **Simulate E-commerce Orders**
   - Runs multiple threads to **simulate users placing orders**.
   - Each order is processed using the **`PlaceOrderProcedure`**, ensuring **ACID compliance**.

5. **Shutdown & Cleanup**
   - After the simulation runs for a set duration, resources are released, and the client disconnects.

---

## **Key Features**
- **High-Concurrency Processing** – Supports thousands of simultaneous transactions.
- **VoltDB Partitioning** – Ensures **fast and efficient** order execution.
- **ACID Compliance** – Transactions are **atomic, consistent, isolated, and durable**.
- **Scalable & Fault-Tolerant** – Can handle **large-scale, real-time workloads**.

---

## **Setup & Execution**
### **Prerequisites**
- **Java Development Kit (JDK)**
- **VoltDB Server** running on the specified hosts.
- **Tables & Stored Procedures**: Ensure the required schema and procedure definitions are loaded.

### **Running the Simulation**
1. **Compile and run the Java client.**
2. **Ensure the necessary stored procedure is deployed in VoltDB.**
3. **Execute simulated e-commerce transactions.**
4. **Monitor transaction logs and performance metrics.**

---

This demo highlights how **VoltDB efficiently handles high-speed, high-concurrency transactions** for an **e-commerce simulation**. 🚀  
Feel free to contact me at biplabec18@gmail.com if you want to clarify something on this or know about Volt Active Data in General

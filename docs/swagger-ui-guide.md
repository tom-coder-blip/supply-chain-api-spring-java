# 🧪 Swagger Testing Guide

This guide explains how to test the **Enterprise Supply Chain API** using Swagger UI.

---

## 🚀 Accessing Swagger
Open the deployed Swagger UI here:  
[Swagger UI](https://supply-chain-api-4sgg.onrender.com/swagger-ui.html)

---

## 🔐 Authentication
1. Use the `/auth/login` endpoint to log in with your role credentials.
2. Copy the returned JWT token.
3. Click the **Authorize** button in Swagger and paste the token.
4. You are now authenticated for your role.

---

## 📦 Testing Endpoints by Role

### Admin
- `/users/register` → Register new users
- `/users/all` → View all users

### Supplier
- `/products/add` → Add new product
- `/products/update` → Update product details
- `/orders/warehouse` → Manage purchase orders

### Warehouse Manager
- `/orders/customer` → Accept/reject customer orders
- `/inventory` → Manage warehouse stock

### Customer
- `/orders/place` → Place new order
- `/orders/cancel` → Cancel pending order

---

## 🔄 Example Workflow
1. **Customer** places an order.
2. **Warehouse Manager** accepts and ships the order.
3. **Supplier** confirms delivery to warehouse.
4. **Admin** audits the transaction.

---

## ✅ Tips
- Always authenticate before testing endpoints.
- Use different role credentials to see RBAC in action.
- Check response codes to confirm permissions are enforced.

- ## 📂 Controller Reference

warehouse-order-controller  
Handles supplier orders delivered to warehouses (e.g., stock receipts).

warehouse-customer-order-controller  
Manages customer orders from the warehouse side — accept, reject, fulfill.

supplier-controller  
Manages supplier profiles and their product catalogs.

supplier-order-controller  
Handles purchase orders initiated by suppliers to restock inventory.

customer-order-controller  
Allows customers to place, view, and cancel orders.

admin-controller  
Oversees user registration, role assignment, and system-wide auditing.

warehouse-controller  
Manages warehouse entities — locations, capacity, and metadata.

stock-receipt-controller  
Records incoming stock deliveries from suppliers to warehouses.

stock-controller  
Tracks and updates stock levels across warehouses.

auth-controller  
Manages login, JWT token generation, and role-based authentication.

inventory-controller  
Provides inventory visibility and operations for warehouse managers.

customer-product-controller  
Lets customers browse available products and view details.


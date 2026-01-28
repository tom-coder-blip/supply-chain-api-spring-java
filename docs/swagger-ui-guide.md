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

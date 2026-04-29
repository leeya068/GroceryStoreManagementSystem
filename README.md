# 🛒 Grocery Store Management System

A comprehensive console-based Grocery Store Management System implemented in Java, featuring inventory management, shopping cart functionality, and undo/redo capabilities using custom data structures.

---

## 🎯 Overview

This project implements a complete Grocery Store Management System with two main modules:
1. **Inventory Management Module** - Manages all product-related operations
2. **Shopping Cart Module** - Handles customer purchases with undo functionality

The system demonstrates the practical application of different data structures based on their strengths and weaknesses in real-world scenarios.

---

## ✨ Features

### Inventory Management
- ✅ Load inventory from text file (`inventory.txt`)
- ✅ Display all products in a formatted table
- ✅ Search products by ID (exact match)
- ✅ Search products by name (case-insensitive, partial match)
- ✅ Add new products (no duplicate IDs allowed)
- ✅ Remove existing products
- ✅ Update stock quantities
- ✅ Save inventory back to file

### Shopping Cart
- ✅ Add items to cart (with stock verification)
- ✅ View cart with itemized subtotals
- ✅ Remove items from cart (restores stock)
- ✅ Update item quantities in cart
- ✅ Clear entire cart
- ✅ Undo last cart addition (using Stack)

### Billing & Exit
- ✅ Generate a detailed bill with the total amount
- ✅ Permanent stock reduction on checkout
- ✅ Save inventory before exit
- ✅ Auto-clear cart and undo stack on checkout

---

## 📊 Data Structures Used

| Module | Data Structure | Justification |
|--------|---------------|---------------|
| **Inventory Management** | `ArrayList<Product>` | • O(1) random access by index<br>• Moderate insertion/deletion<br>• Dynamic resizing capability |
| **Shopping Cart** | Custom `Singly Linked List` | • Sequential access pattern<br>• O(1) insertion/deletion at front/back<br>• No random access needed |
| **Undo Feature** | Custom `Stack` (Linked List-based) | • LIFO access pattern<br>• Push/Pop operations<br>• Restricted interface for clarity |

---

## 📁 Project Structure

'''
📦 grocery-store-management/
│
├── 📂 src/
│   ├── 📄 Product.java
│   ├── 📄 CartNode.java
│   ├── 📄 CartList.java
│   ├── 📄 LinkedListStack.java
│   ├── 📄 CartAction.java
│   ├── 📄 InventoryManager.java
│   └── 🎯 GroceryStoreSystem.java
│
├── 📊 inventory.txt
└── 📖 README.md
'''

---

## 🔧 Prerequisites

- **Java JDK** 8 or higher
- **Any Java IDE** (Eclipse, IntelliJ IDEA, NetBeans) or command-line tools
- Basic understanding of Java and data structures

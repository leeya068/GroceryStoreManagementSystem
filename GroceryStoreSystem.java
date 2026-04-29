import java.util.*;
import java.io.*;

public class GroceryStoreSystem {
    private static InventoryManager inventoryManager;
    private static CartList cart;  // CartList now has its own internal undo stack
    private static Scanner scanner;
    
    public static void main(String[] args) {
        inventoryManager = new InventoryManager();
        cart = new CartList();  // CartList manages its own undo stack internally
        scanner = new Scanner(System.in);
        
        // Load inventory at start
        try {
            inventoryManager.loadFromFile("inventory.txt");
        } catch (IOException e) {
            System.out.println("Error loading inventory: " + e.getMessage());
        }
        
        // Main program loop
        while (true) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    searchProductById();
                    break;
                case 3:
                    searchProductByName();
                    break;
                case 4:
                    addNewProduct();
                    break;
                case 5:
                    removeProduct();
                    break;
                case 6:
                    updateStock();
                    break;
                case 7:
                    addItemToCart();
                    break;
                case 8:
                    viewCart();
                    break;
                case 9:
                    removeItemFromCart();
                    break;
                case 10:
                    updateCartItemQuantity();
                    break;
                case 11:
                    clearCart();
                    break;
                case 12:
                    undoLastAddition();
                    break;
                case 13:
                    checkout();
                    break;
                case 14:
                    saveAndExit();
                    return;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GROCERY STORE MANAGEMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("INVENTORY MANAGEMENT:");
        System.out.println("1. Display All Products");
        System.out.println("2. Search Product by ID");
        System.out.println("3. Search Product by Name");
        System.out.println("4. Add New Product");
        System.out.println("5. Remove Product");
        System.out.println("6. Update Stock");
        System.out.println("\nSHOPPING CART:");
        System.out.println("7. Add Item to Cart");
        System.out.println("8. View Cart");
        System.out.println("9. Remove Item from Cart");
        System.out.println("10. Update Cart Item Quantity");
        System.out.println("11. Clear Cart");
        System.out.println("12. Undo Last Addition");
        System.out.println("\nBILLING:");
        System.out.println("13. Checkout (Generate Bill)");
        System.out.println("14. Save and Exit");
        System.out.println("-".repeat(50));
    }
    
    private static void displayAllProducts() {
        inventoryManager.displayAll();
    }
    
    private static void searchProductById() {
        int id = getIntInput("Enter Product ID: ");
        Product product = inventoryManager.searchById(id);
        if (product != null) {
            System.out.println("\nProduct Found:");
            System.out.printf("%-10s %-25s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
            System.out.println("-".repeat(55));
            System.out.printf("%-10d %-25s %-10.2f %-10d\n", 
                             product.getId(), product.getName(), 
                             product.getPrice(), product.getStock());
        } else {
            System.out.println("Product not found!");
        }
    }
    
    private static void searchProductByName() {
        System.out.print("Enter Product Name (or partial): ");
        String name = scanner.nextLine();
        ArrayList<Product> results = inventoryManager.searchByName(name);
        if (results.isEmpty()) {
            System.out.println("No products found!");
        } else {
            System.out.println("\nMatching Products (" + results.size() + " found):");
            System.out.printf("%-10s %-25s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
            System.out.println("-".repeat(55));
            for (Product p : results) {
                System.out.printf("%-10d %-25s %-10.2f %-10d\n", 
                                 p.getId(), p.getName(), p.getPrice(), p.getStock());
            }
        }
    }
    
    private static void addNewProduct() {
        System.out.println("\n--- Add New Product ---");
        int id = getIntInput("Enter Product ID: ");
        
        if (inventoryManager.searchById(id) != null) {
            System.out.println("Product ID already exists!");
            return;
        }
        
        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine();
        double price = getDoubleInput("Enter Product Price: ");
        int stock = getIntInput("Enter Initial Stock: ");
        
        Product newProduct = new Product(id, name, price, stock);
        inventoryManager.addProduct(newProduct);
    }
    
    private static void removeProduct() {
        int id = getIntInput("Enter Product ID to remove: ");
        inventoryManager.removeProduct(id);
    }
    
    private static void updateStock() {
        int id = getIntInput("Enter Product ID: ");
        int newStock = getIntInput("Enter New Stock Quantity: ");
        inventoryManager.updateStock(id, newStock);
    }
    
    private static void addItemToCart() {
        int id = getIntInput("Enter Product ID: ");
        int quantity = getIntInput("Enter Quantity: ");
        
        Product product = inventoryManager.getProductById(id);
        if (product == null) {
            System.out.println("Product not found!");
            return;
        }
        
        if (!inventoryManager.isAvailable(id, quantity)) {
            System.out.println("Insufficient stock! Available: " + product.getStock());
            return;
        }
        
        // Temporarily reduce stock
        inventoryManager.reduceStock(id, quantity);
        
        // Add to cart (CartList internally pushes to its own undo stack)
        cart.addItem(product, quantity);
        
        System.out.println(quantity + " x " + product.getName() + " added to cart!");
    }
    
    private static void viewCart() {
        cart.displayCart();
    }
    
    private static void removeItemFromCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        
        cart.displayCart();
        int id = getIntInput("Enter Product ID to remove: ");
        
        CartNode item = cart.findItem(id);
        if (item == null) {
            System.out.println("Item not found in cart!");
            return;
        }
        
        // Restore stock
        inventoryManager.restoreStock(id, item.getQuantity());
        
        // Remove from cart
        cart.removeItem(id);
        
        System.out.println("Item removed from cart!");
    }
    
    private static void updateCartItemQuantity() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        
        cart.displayCart();
        int id = getIntInput("Enter Product ID to update: ");
        
        CartNode item = cart.findItem(id);
        if (item == null) {
            System.out.println("Item not found in cart!");
            return;
        }
        
        int newQuantity = getIntInput("Enter new quantity: ");
        int oldQuantity = item.getQuantity();
        
        if (newQuantity > oldQuantity) {
            int additional = newQuantity - oldQuantity;
            if (!inventoryManager.isAvailable(id, additional)) {
                System.out.println("Insufficient stock!");
                return;
            }
            inventoryManager.reduceStock(id, additional);
        } else if (newQuantity < oldQuantity) {
            int reduced = oldQuantity - newQuantity;
            inventoryManager.restoreStock(id, reduced);
        }
        
        // Update quantity (CartList internally pushes to undo stack)
        cart.updateQuantity(id, newQuantity);
        System.out.println("Quantity updated!");
    }
    
    private static void clearCart() {
        if (cart.isEmpty()) {
            System.out.println("Cart is already empty!");
            return;
        }
        
        // Restore all stock
        CartNode current = cart.getHead();
        while (current != null) {
            inventoryManager.restoreStock(current.getProduct().getId(), current.getQuantity());
            current = current.getNext();
        }
        
        cart.clear();  // This also clears the internal undo stack
        System.out.println("Cart cleared!");
    }
    
    private static void undoLastAddition() {
        // Let CartList handle the undo operation and return the undone action
        CartAction undoneAction = cart.undo();
        
        if (undoneAction != null) {
            // Restore stock based on the action type
            Product product = undoneAction.getProduct();
            int quantity = undoneAction.getQuantity();
            String actionType = undoneAction.getActionType();
            
            if (actionType.equals("ADD")) {
                // Restore stock for removed item
                inventoryManager.restoreStock(product.getId(), quantity);
            } else if (actionType.equals("UPDATE")) {
                // Adjust stock based on quantity change
                int oldQuantity = undoneAction.getOldQuantity();
                int newQuantity = quantity;
                int difference = oldQuantity - newQuantity;
                
                if (difference > 0) {
                    // Old quantity was larger, restore the difference
                    inventoryManager.restoreStock(product.getId(), difference);
                } else if (difference < 0) {
                    // New quantity was larger, reduce additional stock
                    inventoryManager.reduceStock(product.getId(), -difference);
                }
            }
        }
    }
    
    private static void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty! Nothing to checkout.");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                BILL");
        System.out.println("=".repeat(60));
        
        cart.displayCart();
        
        double total = cart.calculateTotal();
        System.out.println("\n" + "-".repeat(60));
        System.out.printf("TOTAL AMOUNT: RM%.2f\n", total);
        System.out.println("=".repeat(60));
        
        System.out.print("\nConfirm checkout? (y/n): ");
        String confirm = scanner.nextLine();
        
        if (confirm.equalsIgnoreCase("y")) {
            // Stock is already reduced permanently, just clear cart
            cart.clear();  // This also clears the internal undo stack
            System.out.println("Checkout complete! Thank you for shopping!");
            
            System.out.print("Save inventory to file? (y/n): ");
            String save = scanner.nextLine();
            if (save.equalsIgnoreCase("y")) {
                try {
                    inventoryManager.saveToFile("inventory.txt");
                } catch (IOException e) {
                    System.out.println("Error saving inventory: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Checkout cancelled.");
        }
    }
    
    private static void saveAndExit() {
        try {
            inventoryManager.saveToFile("inventory.txt");
            System.out.println("Inventory saved. Goodbye!");
        } catch (IOException e) {
            System.out.println("Error saving inventory: " + e.getMessage());
        }
    }
    
    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input! Please enter a number: ");
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }
    
    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input! Please enter a number: ");
            scanner.next();
        }
        double value = scanner.nextDouble();
        scanner.nextLine(); // consume newline
        return value;
    }
}
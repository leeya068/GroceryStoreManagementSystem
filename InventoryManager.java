import java.util.*;
import java.io.*;

public class InventoryManager {
    private ArrayList<Product> inventory;
    
    public InventoryManager() {
        inventory = new ArrayList<>();
    }
    
    // Load inventory from file
    public void loadFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Inventory file not found. Starting with empty inventory.");
            return;
        }
        
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) {
                int id = Integer.parseInt(parts[0].trim());
                String name = parts[1].trim();
                double price = Double.parseDouble(parts[2].trim());
                int stock = Integer.parseInt(parts[3].trim());
                inventory.add(new Product(id, name, price, stock));
            }
        }
        reader.close();
        System.out.println("Inventory loaded from " + filename);
    }
    
    // Save inventory to file
    public void saveToFile(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Product p : inventory) {
            writer.write(p.getId() + "," + p.getName() + "," + p.getPrice() + "," + p.getStock());
            writer.newLine();
        }
        writer.close();
        System.out.println("Inventory saved to " + filename);
    }
    
    // Add product (no duplicate ID)
    public boolean addProduct(Product p) {
        if (searchById(p.getId()) != null) {
            System.out.println("Product ID already exists!");
            return false;
        }
        inventory.add(p);
        System.out.println("Product added successfully!");
        return true;
    }
    
    // Remove product by ID
    public boolean removeProduct(int id) {
        Product product = searchById(id);
        if (product != null) {
            inventory.remove(product);
            System.out.println("Product removed successfully!");
            return true;
        }
        System.out.println("Product not found!");
        return false;
    }
    
    // Search product by ID
    public Product searchById(int id) {
        for (Product p : inventory) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
    
    // Search product by name (case-insensitive, partial match)
    public ArrayList<Product> searchByName(String name) {
        ArrayList<Product> results = new ArrayList<>();
        for (Product p : inventory) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }
    
    // Update stock quantity
    public boolean updateStock(int id, int newStock) {
        Product product = searchById(id);
        if (product != null) {
            product.setStock(newStock);
            System.out.println("Stock updated successfully!");
            return true;
        }
        System.out.println("Product not found!");
        return false;
    }
    
    // Display all products
    public void displayAll() {
        if (inventory.isEmpty()) {
            System.out.println("Inventory is empty!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("%-10s %-25s %-10s %-10s\n", "ID", "Name", "Price", "Stock");
        System.out.println("-".repeat(60));
        for (Product p : inventory) {
            System.out.printf("%-10d %-25s %-10.2f %-10d\n", 
                             p.getId(), p.getName(), p.getPrice(), p.getStock());
        }
        System.out.println("=".repeat(60));
    }
    
    // Get product by ID (for cart operations)
    public Product getProductById(int id) {
        return searchById(id);
    }
    
    // Check if sufficient stock exists
    public boolean isAvailable(int id, int requestedQty) {
        Product product = searchById(id);
        if (product != null) {
            return product.getStock() >= requestedQty;
        }
        return false;
    }
    
    // Reduce stock (temporary for cart)
    public boolean reduceStock(int id, int quantity) {
        Product product = searchById(id);
        if (product != null && product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            return true;
        }
        return false;
    }
    
    // Restore stock (for undo/remove from cart)
    public void restoreStock(int id, int quantity) {
        Product product = searchById(id);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
        }
    }
    
    public ArrayList<Product> getInventory() {
        return inventory;
    }
}
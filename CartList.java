public class CartList {
    private CartNode head;
    private int size;
    private LinkedListStack<CartAction> undoStack;  // Add this - Stack for undo functionality
    
    public CartList() {
        head = null;
        size = 0;
        undoStack = new LinkedListStack<>();  // Initialize the undo stack
    }
    
    // Add item to cart (at the end)
    public void addItem(Product product, int quantity) {
        // Check if product already exists in cart
        CartNode existing = findItem(product.getId());
        if (existing != null) {
            int oldQuantity = existing.getQuantity();
            existing.setQuantity(existing.getQuantity() + quantity);
            // Push to undo stack for UPDATE action
            undoStack.push(new CartAction(product, existing.getQuantity(), oldQuantity, "UPDATE"));
        } else {
            CartNode newNode = new CartNode(product, quantity);
            if (head == null) {
                head = newNode;
            } else {
                CartNode current = head;
                while (current.getNext() != null) {
                    current = current.getNext();
                }
                current.setNext(newNode);
            }
            size++;
            // Push to undo stack for ADD action
            undoStack.push(new CartAction(product, quantity, -1, "ADD"));
        }
    }
    
    // Remove item by product ID
    public boolean removeItem(int productId) {
        if (head == null) return false;
        
        if (head.getProduct().getId() == productId) {
            head = head.getNext();
            size--;
            return true;
        }
        
        CartNode current = head;
        while (current.getNext() != null && current.getNext().getProduct().getId() != productId) {
            current = current.getNext();
        }
        
        if (current.getNext() != null) {
            current.setNext(current.getNext().getNext());
            size--;
            return true;
        }
        return false;
    }
    
    // Update quantity of existing cart item
    public boolean updateQuantity(int productId, int newQuantity) {
        CartNode item = findItem(productId);
        if (item != null) {
            int oldQuantity = item.getQuantity();
            item.setQuantity(newQuantity);
            // Push to undo stack for UPDATE action
            undoStack.push(new CartAction(item.getProduct(), newQuantity, oldQuantity, "UPDATE"));
            return true;
        }
        return false;
    }
    
    // Find item by product ID
    public CartNode findItem(int productId) {
        CartNode current = head;
        while (current != null) {
            if (current.getProduct().getId() == productId) {
                return current;
            }
            current = current.getNext();
        }
        return null;
    }
    
    // Display cart contents
    public void displayCart() {
        if (head == null) {
            System.out.println("Cart is empty!");
            return;
        }
        
        System.out.println("\n" + "=".repeat(70));
        System.out.printf("%-5s %-25s %-10s %-10s %-10s\n", 
                         "No.", "Product Name", "Price", "Quantity", "Subtotal");
        System.out.println("-".repeat(70));
        
        CartNode current = head;
        int index = 1;
        while (current != null) {
            Product p = current.getProduct();
            double subtotal = p.getPrice() * current.getQuantity();
            System.out.printf("%-5d %-25s %-10.2f %-10d %-10.2f\n", 
                             index++, p.getName(), p.getPrice(), 
                             current.getQuantity(), subtotal);
            current = current.getNext();
        }
        System.out.println("=".repeat(70));
        System.out.printf("TOTAL: RM%.2f\n", calculateTotal());
    }
    
    // Calculate total bill amount
    public double calculateTotal() {
        double total = 0;
        CartNode current = head;
        while (current != null) {
            total += current.getProduct().getPrice() * current.getQuantity();
            current = current.getNext();
        }
        return total;
    }
    
    // Clear entire cart
    public void clear() {
        head = null;
        size = 0;
        undoStack.clear();  // Also clear the undo stack
    }
    
    // Undo method - removes the last added item/product from cart
    public CartAction undo() {
        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo!");
            return null;
        }
        
        CartAction lastAction = undoStack.pop();
        Product product = lastAction.getProduct();
        int quantity = lastAction.getQuantity();
        String actionType = lastAction.getActionType();
        
        if (actionType.equals("ADD")) {
            // Remove the item from cart
            removeItem(product.getId());
            System.out.println("Undo: Removed " + quantity + " x " + product.getName() + " from cart");
        } else if (actionType.equals("UPDATE")) {
            // Restore the old quantity
            int oldQuantity = lastAction.getOldQuantity();
            updateQuantity(product.getId(), oldQuantity);
            System.out.println("Undo: Restored " + product.getName() + " quantity to " + oldQuantity);
        }
        
        return lastAction;
    }
    
    // Check if undo is available
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return head == null;
    }

    // Get the head node (for traversing the cart)
    public CartNode getHead() {
        return head;
    }
}
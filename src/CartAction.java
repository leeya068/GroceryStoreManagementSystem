package src;
public class CartAction {
    private Product product;
    private int quantity;
    private int oldQuantity;  // For update operations
    private String actionType;  // "ADD" or "UPDATE"
    
    // Constructor for ADD action
    public CartAction(Product product, int quantity, int oldQuantity, String actionType) {
        this.product = product;
        this.quantity = quantity;
        this.oldQuantity = oldQuantity;
        this.actionType = actionType;
    }
    
    // Getters
    public Product getProduct() {
        return product;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public int getOldQuantity() {
        return oldQuantity;
    }
    
    public String getActionType() {
        return actionType;
    }
}
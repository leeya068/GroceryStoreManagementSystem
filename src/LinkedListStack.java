package src;
public class LinkedListStack<T> {
    private Node<T> top;
    private int size;
    
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public LinkedListStack() {
        top = null;
        size = 0;
    }
    
    // Push onto stack
    public void push(T item) {
        Node<T> newNode = new Node<>(item);
        newNode.next = top;
        top = newNode;
        size++;
    }
    
    // Pop from stack
    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T data = top.data;
        top = top.next;
        size--;
        return data;
    }
    
    // Peek at top item
    public T peek() {
        return isEmpty() ? null : top.data;
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public void clear() {
        top = null;
        size = 0;
    }
}
package com.example.mobilak_p;

public class Ticket {
    private String id;
    private String name;
    private int price;
    private int quantity = 0;

    public Ticket() {} // Üres konstruktor Firestore-hoz

    public Ticket(String id, String name, int price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}

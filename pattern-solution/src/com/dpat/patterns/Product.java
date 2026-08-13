package com.dpat.patterns;

/**
 * Immutable value object representing a catalogue product.
 */
public class Product {
    private final String sku;
    private final String name;
    private final double price;
    private final int quantity;

    public Product(String sku, String name, double price, int quantity) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

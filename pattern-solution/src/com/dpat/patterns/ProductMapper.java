package com.dpat.patterns;

import java.util.Map;

/**
 * Maps raw record maps to Product entities and validates product invariants.
 */
public class ProductMapper {

    public Product map(Map<String, String> record) throws ValidationException {
        String sku = record.get("sku");
        String name = record.get("name");
        String priceText = record.get("price");
        String quantityText = record.get("quantity");

        if (sku == null || name == null || priceText == null || quantityText == null) {
            throw new ValidationException("missing field");
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new ValidationException("price is not a number: " + priceText);
        }
        try {
            quantity = Integer.parseInt(quantityText);
        } catch (NumberFormatException e) {
            throw new ValidationException("quantity is not a number: " + quantityText);
        }

        return new Product(sku, name, price, quantity);
    }

    public void validate(Product p) throws ValidationException {
        if (p.getSku().trim().length() == 0) {
            throw new ValidationException("sku is empty");
        }
        if (p.getName().trim().length() == 0) {
            throw new ValidationException("name is empty");
        }
        if (p.getPrice() < 0) {
            throw new ValidationException("price is negative");
        }
        if (p.getQuantity() < 0) {
            throw new ValidationException("quantity is negative");
        }
    }
}

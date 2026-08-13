package com.dpat.simple;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the outcome of an import operation.
 */
public class ImportResult {
    private final List<Product> products;
    private final List<String> errors;

    public ImportResult() {
        this.products = new ArrayList<Product>();
        this.errors = new ArrayList<String>();
    }

    public void addProduct(Product p) {
        products.add(p);
    }

    public void addError(String message) {
        errors.add(message);
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<String> getErrors() {
        return errors;
    }

    public int getSuccessCount() {
        return products.size();
    }

    public int getFailureCount() {
        return errors.size();
    }
}

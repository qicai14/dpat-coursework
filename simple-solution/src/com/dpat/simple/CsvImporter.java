package com.dpat.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Imports product data from CSV files.
 */
public class CsvImporter {

    public ImportResult read(Path file) throws IOException {
        ImportResult result = new ImportResult();
        List<Map<String, String>> records = parseRecords(file);
        for (int i = 0; i < records.size(); i++) {
            try {
                Product p = mapFields(records.get(i));
                validate(p);
                result.addProduct(p);
            } catch (ValidationException e) {
                result.addError("Record " + (i + 1) + ": " + e.getMessage());
            }
        }
        return result;
    }

    private List<Map<String, String>> parseRecords(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        if (lines.isEmpty()) {
            return records;
        }

        String headerLine = lines.get(0);
        String[] headers = headerLine.split(",");
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.length() == 0) {
                continue;
            }
            String[] values = line.split(",");
            Map<String, String> record = new HashMap<String, String>();
            for (int j = 0; j < headers.length && j < values.length; j++) {
                record.put(headers[j], values[j].trim());
            }
            records.add(record);
        }
        return records;
    }

    private Product mapFields(Map<String, String> record) throws ValidationException {
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

    private void validate(Product p) throws ValidationException {
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

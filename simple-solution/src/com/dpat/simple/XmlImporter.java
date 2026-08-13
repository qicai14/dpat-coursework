package com.dpat.simple;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Imports product data from XML files.
 */
public class XmlImporter {

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
        byte[] bytes = Files.readAllBytes(file);
        String content = new String(bytes, "UTF-8");
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();

        String[] tags = new String[] { "sku", "name", "price", "quantity" };
        int searchPos = 0;
        while (true) {
            int startProduct = content.indexOf("<product>", searchPos);
            if (startProduct == -1) {
                break;
            }
            int endProduct = content.indexOf("</product>", startProduct);
            if (endProduct == -1) {
                break;
            }
            String productBlock = content.substring(startProduct + "<product>".length(), endProduct);
            Map<String, String> record = new HashMap<String, String>();
            for (int t = 0; t < tags.length; t++) {
                String openTag = "<" + tags[t] + ">";
                String closeTag = "</" + tags[t] + ">";
                int tagStart = productBlock.indexOf(openTag);
                int tagEnd = productBlock.indexOf(closeTag);
                if (tagStart != -1 && tagEnd != -1 && tagEnd >= tagStart + openTag.length()) {
                    String val = productBlock.substring(tagStart + openTag.length(), tagEnd).trim();
                    record.put(tags[t], val);
                }
            }
            records.add(record);
            searchPos = endProduct + "</product>".length();
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

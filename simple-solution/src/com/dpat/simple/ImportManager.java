package com.dpat.simple;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Manages the import process by delegating to format-specific importers.
 */
public class ImportManager {

    public ImportResult importFile(Path file) throws IOException {
        String ext = extensionOf(file);
        if (ext.equals("csv")) {
            CsvImporter importer = new CsvImporter();
            return importer.read(file);
        } else if (ext.equals("json")) {
            JsonImporter importer = new JsonImporter();
            return importer.read(file);
        } else if (ext.equals("xml")) {
            XmlImporter importer = new XmlImporter();
            return importer.read(file);
        } else if (ext.equals("txt")) {
            return importLegacy(file);
        } else {
            ImportResult result = new ImportResult();
            result.addError("Unsupported file type: " + ext);
            return result;
        }
    }

    private String extensionOf(Path file) {
        String name = file.getFileName().toString();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1 || lastDot == name.length() - 1) {
            return "";
        }
        return name.substring(lastDot + 1).toLowerCase();
    }

    private ImportResult importLegacy(Path file) throws IOException {
        ImportResult result = new ImportResult();
        LegacyFlatFileReader reader = new LegacyFlatFileReader();
        String[][] rows = reader.readAll(file.toFile());
        for (int i = 0; i < rows.length; i++) {
            try {
                Product p = mapLegacyRow(rows[i]);
                validate(p);
                result.addProduct(p);
            } catch (ValidationException e) {
                result.addError("Record " + (i + 1) + ": " + e.getMessage());
            }
        }
        return result;
    }

    private Product mapLegacyRow(String[] row) throws ValidationException {
        if (row == null || row.length < 4) {
            throw new ValidationException("missing field");
        }
        String sku = row[0];
        String name = row[1];
        String priceText = row[2];
        String quantityText = row[3];

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

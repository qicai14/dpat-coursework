package com.dpat.simple;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

/**
 * Command-line entry point for importing product catalogue files.
 */
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: Main <file>");
            return;
        }

        try {
            ImportManager manager = new ImportManager();
            ImportResult result = manager.importFile(Paths.get(args[0]));
            System.out.println("Import complete: " + result.getSuccessCount() + " succeeded, " + result.getFailureCount() + " failed");
            if (result.getFailureCount() > 0) {
                System.out.println();
                System.out.println("Errors:");
                List<String> errors = result.getErrors();
                for (int i = 0; i < errors.size(); i++) {
                    System.out.println("  " + errors.get(i));
                }
            }
            List<Product> products = result.getProducts();
            for (int i = 0; i < products.size(); i++) {
                Product p = products.get(i);
                System.out.println(p.getSku() + " | " + p.getName() + " | " + String.format(Locale.US, "%.2f", p.getPrice()) + " | " + p.getQuantity());
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }
}

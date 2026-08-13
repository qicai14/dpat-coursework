package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Path;
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
            Path file = Paths.get(args[0]);
            ImportJob job = jobFor(file);
            ImportResult result;
            if (job != null) {
                result = job.run(file);
            } else {
                String name = file.getFileName().toString();
                int dot = name.lastIndexOf('.');
                String ext = "";
                if (dot >= 0 && dot < name.length() - 1) {
                    ext = name.substring(dot + 1).toLowerCase();
                }
                result = new ImportResult();
                result.addError("Unsupported file type: " + ext);
            }

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

    private static ImportJob jobFor(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        String ext = "";
        if (dot >= 0 && dot < name.length() - 1) {
            ext = name.substring(dot + 1).toLowerCase();
        }
        if (ext.equals("csv")) {
            return new CsvImportJob();
        } else if (ext.equals("json")) {
            return new JsonImportJob();
        } else if (ext.equals("xml")) {
            return new XmlImportJob();
        } else if (ext.equals("txt")) {
            return new LegacyImportJob();
        } else {
            return null;
        }
    }
}

package com.dpat.patterns;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Legacy component that reads fixed-width text files into string arrays.
 */
public class LegacyFlatFileReader {

    public String[][] readAll(File file) throws IOException {
        List<String[]> records = new ArrayList<String[]>();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                String sku = "";
                String name = "";
                String price = "";
                String quantity = "";

                if (line.length() >= 10) {
                    sku = line.substring(0, 10).trim();
                } else {
                    sku = line.substring(0).trim();
                }

                if (line.length() >= 40) {
                    name = line.substring(10, 40).trim();
                } else if (line.length() > 10) {
                    name = line.substring(10).trim();
                }

                if (line.length() >= 50) {
                    price = line.substring(40, 50).trim();
                } else if (line.length() > 40) {
                    price = line.substring(40).trim();
                }

                if (line.length() >= 58) {
                    quantity = line.substring(50, 58).trim();
                } else if (line.length() > 50) {
                    quantity = line.substring(50).trim();
                }

                records.add(new String[] { sku, name, price, quantity });
            }
        } finally {
            reader.close();
        }
        return records.toArray(new String[records.size()][]);
    }
}

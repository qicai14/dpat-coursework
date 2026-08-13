package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter adapting LegacyFlatFileReader to the ProductParser interface.
 */
public class LegacyFlatFileAdapter implements ProductParser {
    private final LegacyFlatFileReader reader;

    public LegacyFlatFileAdapter() {
        this.reader = new LegacyFlatFileReader();
    }

    public List<Map<String, String>> parse(Path file) throws IOException {
        String[][] rows = reader.readAll(file.toFile());
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        for (int i = 0; i < rows.length; i++) {
            Map<String, String> record = new HashMap<String, String>();
            record.put("sku", rows[i][0]);
            record.put("name", rows[i][1]);
            record.put("price", rows[i][2]);
            record.put("quantity", rows[i][3]);
            records.add(record);
        }
        return records;
    }
}

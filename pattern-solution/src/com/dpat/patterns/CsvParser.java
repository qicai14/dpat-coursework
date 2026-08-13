package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses CSV product files into raw record maps.
 */
public class CsvParser implements ProductParser {

    public List<Map<String, String>> parse(Path file) throws IOException {
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
}

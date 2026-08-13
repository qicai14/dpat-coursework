package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses JSON product files into raw record maps.
 */
public class JsonParser implements ProductParser {

    public List<Map<String, String>> parse(Path file) throws IOException {
        List<String> lines = Files.readAllLines(file);
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (!line.contains("{")) {
                continue;
            }
            int start = line.indexOf('{');
            int end = line.lastIndexOf('}');
            if (start != -1 && end != -1 && end > start) {
                String content = line.substring(start + 1, end).trim();
                String[] pairs = content.split(",");
                Map<String, String> record = new HashMap<String, String>();
                for (int j = 0; j < pairs.length; j++) {
                    String pair = pairs[j].trim();
                    int colonIndex = pair.indexOf(':');
                    if (colonIndex != -1) {
                        String key = pair.substring(0, colonIndex).trim().replace("\"", "");
                        String value = pair.substring(colonIndex + 1).trim().replace("\"", "");
                        record.put(key, value);
                    }
                }
                records.add(record);
            }
        }
        return records;
    }
}

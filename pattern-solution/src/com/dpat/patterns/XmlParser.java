package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses XML product files into raw record maps.
 */
public class XmlParser implements ProductParser {

    public List<Map<String, String>> parse(Path file) throws IOException {
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
}

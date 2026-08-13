package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Target interface declaring the contract for parsing product files into records.
 */
public interface ProductParser {
    List<Map<String, String>> parse(Path file) throws IOException;
}

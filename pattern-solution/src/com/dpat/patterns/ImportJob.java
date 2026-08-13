package com.dpat.patterns;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Abstract Creator defining the template import workflow and factory method.
 */
public abstract class ImportJob {

    public ImportResult run(Path file) throws IOException {
        ImportResult result = new ImportResult();
        ProductParser parser = createParser();
        ProductMapper mapper = new ProductMapper();
        List<Map<String, String>> records = parser.parse(file);
        for (int i = 0; i < records.size(); i++) {
            try {
                Product p = mapper.map(records.get(i));
                mapper.validate(p);
                result.addProduct(p);
            } catch (ValidationException e) {
                result.addError("Record " + (i + 1) + ": " + e.getMessage());
            }
        }
        return result;
    }

    protected abstract ProductParser createParser();
}

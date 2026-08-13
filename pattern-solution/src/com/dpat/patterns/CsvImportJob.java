package com.dpat.patterns;

/**
 * Concrete Creator for CSV import jobs.
 */
public class CsvImportJob extends ImportJob {

    protected ProductParser createParser() {
        return new CsvParser();
    }
}

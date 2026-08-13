package com.dpat.patterns;

/**
 * Concrete Creator for legacy flat file import jobs.
 */
public class LegacyImportJob extends ImportJob {

    protected ProductParser createParser() {
        return new LegacyFlatFileAdapter();
    }
}

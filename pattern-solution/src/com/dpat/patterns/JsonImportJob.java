package com.dpat.patterns;

/**
 * Concrete Creator for JSON import jobs.
 */
public class JsonImportJob extends ImportJob {

    protected ProductParser createParser() {
        return new JsonParser();
    }
}

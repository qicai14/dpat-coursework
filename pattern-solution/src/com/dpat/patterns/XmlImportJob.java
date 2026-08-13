package com.dpat.patterns;

/**
 * Concrete Creator for XML import jobs.
 */
public class XmlImportJob extends ImportJob {

    protected ProductParser createParser() {
        return new XmlParser();
    }
}

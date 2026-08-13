package com.dpat.patterns;

/**
 * Checked exception thrown when product data fails validation.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

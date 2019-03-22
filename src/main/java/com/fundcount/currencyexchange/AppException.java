package com.fundcount.currencyexchange;

/**
 * Прикладные исключения
 */
public abstract class AppException extends Exception {

    public AppException(Throwable cause) {
        super(cause);
    }

    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public static class ParseException extends AppException {
        public ParseException(Throwable cause) {
            super(cause);
        }
    }

    public static class RatesRepositoryException extends AppException {
        public RatesRepositoryException(String message) {
            super(message);
        }
    }

    public static class UnknownCurrencyException extends AppException {
        public UnknownCurrencyException(String currency) {
            super("Unknown currency " + currency);
        }
    }

    public static class CurrencyNotFoundException extends AppException {
        public CurrencyNotFoundException(String message) {
            super(message);
        }
    }

    public static class InputParseException extends AppException {
        public InputParseException(String message) {
            super(message);
        }

        public InputParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

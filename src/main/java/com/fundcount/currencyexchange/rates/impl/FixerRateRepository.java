package com.fundcount.currencyexchange.rates.impl;

/**
 * Реализация репозитория для fixer.io
 * https://fixer.io/documentation
 */
public class FixerRateRepository extends AbstractRateRepository {

    private static final String URL = "http://data.fixer.io/api/latest?";

    public FixerRateRepository(String apiKey) {
        super(URL, apiKey, false);
    }
}

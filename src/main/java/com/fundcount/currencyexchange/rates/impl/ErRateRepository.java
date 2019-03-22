package com.fundcount.currencyexchange.rates.impl;

/**
 * Реализация репозитория для exchangeratesapi.io
 * https://exchangeratesapi.io/
 */

public class ErRateRepository extends AbstractRateRepository {

    private static final String URL = "https://api.exchangeratesapi.io/";

    public ErRateRepository() {
        super(URL, true);
    }
}

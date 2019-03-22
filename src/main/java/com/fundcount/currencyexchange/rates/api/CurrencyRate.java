package com.fundcount.currencyexchange.rates.api;

/**
 * Курс валюты
 */
public class CurrencyRate {

    private Currency baseCurrency;
    private Currency correctionCurrency;
    private double rate;

    public CurrencyRate(Currency baseCurrency, Currency correctionCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.correctionCurrency = correctionCurrency;
        this.rate = rate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public Currency getCorrectionCurrency() {
        return correctionCurrency;
    }

    public double getRate() {
        return rate;
    }
}

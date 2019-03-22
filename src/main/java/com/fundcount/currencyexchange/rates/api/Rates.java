package com.fundcount.currencyexchange.rates.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Модель ответа репозитория
 */
public class Rates {
    private Date day;
    private Currency baseCurrency;
    private Map<Currency, CurrencyRate> rates;

    public Rates(Date day, Currency baseCurrency) {
        this.day = day;
        this.baseCurrency = baseCurrency;
    }

    public Date getDay() {
        return day;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Map<Currency, CurrencyRate> getRates() {
        return rates;
    }

    public void setRates(Map<Currency, CurrencyRate> rates) {
        this.rates = rates;
    }

    public void addRate(CurrencyRate rate) {
        if (rates == null) {
            rates = new HashMap<Currency, CurrencyRate>();
        }
        rates.put(rate.getCorrectionCurrency(), rate);
    }
}

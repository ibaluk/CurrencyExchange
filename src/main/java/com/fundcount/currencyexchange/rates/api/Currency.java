package com.fundcount.currencyexchange.rates.api;

import com.fundcount.currencyexchange.AppException;

/**
 * Валюта
 */
public enum Currency {

    USD(840), RUB(643), EUR(978);

    private int code;

    Currency(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static Currency findByName(String name) throws AppException.UnknownCurrencyException {
        if (name == null) {
            throw new AppException.UnknownCurrencyException(name);
        }
        for (Currency currency : Currency.values()) {
            if (currency.toString().equals(name)) {
                return currency;
            }
        }
        throw new AppException.UnknownCurrencyException(name);
    }
}

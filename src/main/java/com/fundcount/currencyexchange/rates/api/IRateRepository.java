package com.fundcount.currencyexchange.rates.api;

import com.fundcount.currencyexchange.AppException;

import java.util.Collection;
import java.util.Date;

public interface IRateRepository {

    Rates getRates(Date day, Currency baseCurrency, Collection<Currency> ratesCurrency) throws AppException;

    Rates getRates(Date day, Currency baseCurrency, Currency ratesCurrency) throws AppException;
}

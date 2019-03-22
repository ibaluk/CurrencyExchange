package com.fundcount.currencyexchange.rates.api;

import com.fundcount.currencyexchange.AppException;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public abstract class AbstractRateRepository implements IRateRepository {

    public Rates getRates(Date day, Currency baseCurrency, Currency ratesCurrency) throws AppException {
        return getRates(day, baseCurrency, Collections.singletonList(ratesCurrency));
    }
}

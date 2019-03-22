package com.fundcount.currencyexchange.rates.mock;

import com.fundcount.currencyexchange.AppException;
import com.fundcount.currencyexchange.rates.api.AbstractRateRepository;
import com.fundcount.currencyexchange.rates.api.Currency;
import com.fundcount.currencyexchange.rates.api.CurrencyRate;
import com.fundcount.currencyexchange.rates.api.Rates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Заглушка для получения статических курсов
 */
public class MockRateRepository extends AbstractRateRepository {

    public static Map<Currency, CurrencyRate> fixedRates = Maps.newHashMap(ImmutableMap.of(
            Currency.USD, new CurrencyRate(Currency.USD, Currency.RUB, 63.76d),
            Currency.EUR, new CurrencyRate(Currency.EUR, Currency.RUB, 71.52d)
    ));

    public Rates getRates(Date day, Currency baseCurrency, Collection<Currency> ratesCurrency) throws AppException {
        Rates rates = new Rates(day, baseCurrency);
        if (fixedRates.containsKey(baseCurrency)) {
            for (Currency currency : ratesCurrency) {
                CurrencyRate currencyRate = fixedRates.get(baseCurrency);

                Random random = new Random();
                //уменешение/увеличение статического курса на 0.1%-0.9% с равной вероятностью
                double percent = 1 + (((float) random.nextInt(9) + 1) / 1000) * (random.nextInt(2) == 1 ? -1 : 1);
                rates.addRate(new CurrencyRate(baseCurrency, currency, currencyRate.getRate() * percent));
            }
        }
        return rates;
    }
}

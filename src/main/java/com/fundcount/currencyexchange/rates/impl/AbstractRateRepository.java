package com.fundcount.currencyexchange.rates.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fundcount.currencyexchange.AppException;
import com.fundcount.currencyexchange.rates.api.Currency;
import com.fundcount.currencyexchange.rates.api.CurrencyRate;
import com.fundcount.currencyexchange.rates.api.Rates;
import com.fundcount.currencyexchange.utils.HttpUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Абстракция для однотипных репозиториев
 */
public abstract class AbstractRateRepository extends com.fundcount.currencyexchange.rates.api.AbstractRateRepository {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Ключ доступа (не для всех API)
     */
    private String apiKey;
    /**
     * Базовый адрес
     */
    private String baseUrl;
    /**
     * Признак указания дня в пути адреса
     */
    private boolean isPathDay;

    public AbstractRateRepository(String baseUrl, String apiKey, boolean isPathDay) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.isPathDay = isPathDay;
    }

    public AbstractRateRepository(String baseUrl, boolean isPathDay) {
        this.baseUrl = baseUrl;
        this.isPathDay = isPathDay;
    }

    public Rates getRates(Date day, Currency baseCurrency, Collection<Currency> ratesCurrency) throws AppException {
        //собираем валюты
        StringBuilder symbolsSb = new StringBuilder();
        for (Currency currency : ratesCurrency) {
            if (symbolsSb.length() > 0) {
                symbolsSb.append(",");
            }
            symbolsSb.append(currency.toString());
        }
        String reqDay = dateFormat.format(day);
        HashMap<String, String> params = Maps.newHashMap(ImmutableMap.of(
                "base", baseCurrency.toString(),
                "symbols", symbolsSb.toString()));
        if (apiKey != null) {
            params.put("access_key", apiKey);
        }
        if (!isPathDay) {
            params.put("date", reqDay);
        }

        String url = HttpUtils.buildUrl(baseUrl, isPathDay ? reqDay : null, params);
        //отправка запроса
        JsonNode response = HttpUtils.parseJson(HttpUtils.sendGet(url));

        boolean resSuccess = response.has("success") && Boolean.parseBoolean(response.get("success").asText());

        //проверка параметров ответа
        if (resSuccess || ((response.has("date") && response.has("base") && response.has("rates")))) {
            if (response.has("date") && response.has("base") && response.has("rates")) {
                //TODO ibaluk: иногда сервис вернет курс не по запрошенной дате, а за последнюю дату торгов
//                if (!response.get("date").asText().equals(reqDay)) {
//                    throw new AppException.RatesRepositoryException("Days is not equals");
//                }
                if (!response.get("base").asText().equals(baseCurrency.toString())) {
                    throw new AppException.RatesRepositoryException("Base currency is not equals");
                }
                Rates rates = null;
                try {
                    rates = new Rates(dateFormat.parse(response.get("date").asText()), baseCurrency);
                } catch (ParseException e) {
                    throw new AppException.RatesRepositoryException("Wrong date");
                }

                JsonNode resRates = response.get("rates");
                Iterator<String> currencyNames = resRates.fieldNames();
                while (currencyNames.hasNext()) {
                    try {
                        String currencyName = currencyNames.next();
                        Currency currency = Currency.findByName(currencyName);
                        rates.addRate(new CurrencyRate(baseCurrency, currency, Double.parseDouble(resRates.get(currencyName).asText())));
                    } catch (AppException.UnknownCurrencyException e) {
                        //ignore
                    } catch (NumberFormatException e) {
                        //ignore
                    }
                }
                return rates;
            } else {
                throw new AppException.RatesRepositoryException("Required parameters is not set in the response");

            }
        } else {
            //обрабатываем ошибку сервиса
            if (response.has("error")) {
                StringBuilder sb = new StringBuilder();
                if (response.get("error").has("code")) {
                    sb.append(response.get("error").get("code").asText()).append(" ");
                }
                if (response.get("error").has("type")) {
                    sb.append(response.get("error").get("type").asText());
                }
                if (sb.length() > 0) {
                    throw new AppException.RatesRepositoryException(sb.toString());
                } else {
                    throw new AppException.RatesRepositoryException("Unknown error");
                }
            } else {
                throw new AppException.RatesRepositoryException("Unknown error");
            }
        }
    }
}

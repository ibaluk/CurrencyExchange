package com.fundcount.currencyexchange.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fundcount.currencyexchange.AppException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

public class HttpUtils {

    /**
     * Отправка Get-запроса
     * @param url адрес для отправки
     * @return тело ответа
     */
    public static String sendGet(String url) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> s = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return s.getBody();
    }

    /**
     * Формирование URL с get-параметрами
     * @param baseUrl базовый адрес
     * @param pathParam параметр стоящий в адресе запроса
     * @param params карта Get-параметров
     * @return
     */
    public static String buildUrl(final String baseUrl, String pathParam, Map<String, String> params) {
        StringBuilder urlSb = new StringBuilder(baseUrl);
        if (!baseUrl.endsWith("/")) {
            urlSb.append("/");
        }
        if (pathParam != null) {
            urlSb.append(pathParam);
        }
        if (!baseUrl.endsWith("?")) {
            urlSb.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            urlSb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return urlSb.toString();
    }

    /**
     * Парсинг JSON-строки
     * @param s исходная строка
     * @return объект JsonNode - результат парсинга
     * @throws AppException.ParseException
     */
    public static JsonNode parseJson(String s) throws AppException.ParseException {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readTree(s);
        } catch (IOException e) {
            throw new AppException.ParseException(e);
        }
    }
}

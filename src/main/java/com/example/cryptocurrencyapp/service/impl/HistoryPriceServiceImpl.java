package com.example.cryptocurrencyapp.service.impl;

import com.example.cryptocurrencyapp.dto.ApiHistoryPriceDto;
import com.example.cryptocurrencyapp.service.HistoryPriceService;
import com.example.cryptocurrencyapp.service.ResponseParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HistoryPriceServiceImpl implements HistoryPriceService {
    private static final String REQUEST_PREFIX = "/COINBASE_SPOT_";
    private static final String REQUEST_SUFFIX = "_USD/history?time_start=";
    private static final String REQUEST_TAIL = "&limit=20";
    private final ResponseParser responseParser;
    @Value(value = "${historyRestApiLink}")
    private String link;
    @Value(value = "${API_KEY}")
    private String key;

    @Override
    public List<ApiHistoryPriceDto> getHistoryPrice(String cryptoName, String date) {
        String toSite = link + REQUEST_PREFIX + cryptoName + REQUEST_SUFFIX + date + REQUEST_TAIL;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(toSite)
                .addHeader("X-CoinAPI-Key", key)
                .build();
        try {
            Response response = client.newCall(request).execute();
            ApiHistoryPriceDto[] apiHistoryPriceDtos = responseParser
                    .parse(response, ApiHistoryPriceDto[].class);
            return Arrays.stream(apiHistoryPriceDtos).toList();
        } catch (IOException e) {
            throw new RuntimeException("Cannot execute request to url: "
                    + toSite + System.lineSeparator() + e);
        }
    }
}

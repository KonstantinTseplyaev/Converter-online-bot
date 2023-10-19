package com.example.firsttestbot.service.impl;

import com.example.firsttestbot.entyty.Currency;
import com.example.firsttestbot.service.CurrencyConversionService;
import lombok.SneakyThrows;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NbrbCurrencyConversionService implements CurrencyConversionService {

    @Override
    public double getConversionRatio(Currency original, Currency target) {
        double originalRate = getRate(original);
        double targetRate = getRate(target);
        return originalRate / targetRate;
    }

    @SneakyThrows
    private double getRate(Currency currency) {
        URL url = new URL("https://api.nbrb.by/exrates/rates/" + currency.getId());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader rb = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = rb.readLine()) != null) {
            response.append(inputLine);
        }
        rb.close();
        JSONObject object = new JSONObject(response.toString());
        double rate = object.getDouble("Cur_OfficialRate");
        double scale = object.getDouble("Cur_Scale");
        return rate / scale;
    }
}

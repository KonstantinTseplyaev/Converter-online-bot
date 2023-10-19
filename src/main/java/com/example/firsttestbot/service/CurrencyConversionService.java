package com.example.firsttestbot.service;

import com.example.firsttestbot.entyty.Currency;
import com.example.firsttestbot.service.impl.NbrbCurrencyConversionService;

public interface CurrencyConversionService {

    static CurrencyConversionService getInstance() {
        return new NbrbCurrencyConversionService();
    }

    double getConversionRatio(Currency original, Currency target);
}

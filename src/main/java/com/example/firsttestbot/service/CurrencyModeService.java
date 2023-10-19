package com.example.firsttestbot.service;

import com.example.firsttestbot.entyty.Currency;
import com.example.firsttestbot.service.impl.HashMapCurrencyModeService;



public interface CurrencyModeService {

    static CurrencyModeService getInstance() {
        return new HashMapCurrencyModeService();
    }

    Currency getOriginalCurrency(long chatId);

    Currency getTargetCurrency(long chatId);

    void setOriginalCurrency(long chatId, Currency currency);

    void setTargetCurrency(long chatId, Currency currency);
}

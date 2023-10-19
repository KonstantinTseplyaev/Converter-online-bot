package com.example.firsttestbot.entyty;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Currency {
    USD(431),
    EUR(451),
    GBP(429),
    RUB(456),
    CNY(462),
    JPY(508);
    private final int id;
}

package com.example.cryptocurrencyapp.util;

public class CryptoSymbolConverter {
    public static String toSymbolId(String crypto) {
        return "COINBASE_SPOT_" + crypto + "_USD";
    }
}

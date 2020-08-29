package uk.co.mgntech.currency_converter.utils

import java.util.Currency
import java.util.Locale

class CurrencyExtensions {
    companion object {
        private val currencyLocaleMap = init()
        val currencyCountryMap = countryInit()
        val currencyDisplayCountryMap = displayCountryInit()
        private fun init(): MutableMap<Currency, MutableList<Locale>> {
            val map = emptyMap<Currency, MutableList<Locale>>().toMutableMap()
            Locale.getAvailableLocales().forEach {
                try {
                    val instance = Currency.getInstance(it)
                    if (map[instance] == null) map[instance] =
                        mutableListOf(it) else map[instance]?.add(it)
                } catch (_: Exception) {
                }
            }
            return map
        }

        private fun countryInit(): Map<Currency, String> {
            val newKeysMap = mutableMapOf<Currency, String>()
            return currencyLocaleMap.mapValuesTo(newKeysMap) {
                currencyLocaleMap[it.key]?.groupingBy { locale -> locale.country }
                    ?.eachCount()?.entries?.maxByOrNull { entry -> entry.value }?.key.orEmpty()
            }
        }

        private fun displayCountryInit(): Map<Currency, String> {
            val newKeysMap = mutableMapOf<Currency, String>()
            return currencyLocaleMap.mapValuesTo(newKeysMap) {
                currencyLocaleMap[it.key]?.groupingBy { locale -> locale.displayCountry }
                    ?.eachCount()?.entries?.maxByOrNull { entry -> entry.value }?.key.orEmpty()
            }
        }
    }
}

fun Currency.country(): String {
    return if (this.currencyCode == "EUR") {
        "EU"
    } else {
        CurrencyExtensions.currencyCountryMap[this].orEmpty()
    }
}

fun Currency.displayCountry(): String {
    return if (this.currencyCode == "EUR") {
        "Europe"
    } else {
        CurrencyExtensions.currencyDisplayCountryMap[this].orEmpty()
    }
}

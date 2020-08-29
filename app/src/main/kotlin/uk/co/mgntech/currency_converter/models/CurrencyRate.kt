package uk.co.mgntech.currency_converter.models

import com.google.gson.annotations.SerializedName

data class CurrencyRate(
    @SerializedName("baseCurrency")
    val baseCurrency: String,
    @SerializedName("rates")
    val rates: Map<String, Double>
)

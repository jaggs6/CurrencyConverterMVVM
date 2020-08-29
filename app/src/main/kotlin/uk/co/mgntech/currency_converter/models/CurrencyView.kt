package uk.co.mgntech.currency_converter.models

import androidx.lifecycle.MutableLiveData
import java.util.Currency

data class CurrencyView(val currency: Currency, val rate: MutableLiveData<Double>)

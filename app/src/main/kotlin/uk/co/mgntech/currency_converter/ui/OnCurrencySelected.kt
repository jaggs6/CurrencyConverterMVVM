package uk.co.mgntech.currency_converter.ui

import uk.co.mgntech.currency_converter.models.CurrencyView

interface OnCurrencySelected {
    fun onCurrencySelected(position: CurrencyView)
}

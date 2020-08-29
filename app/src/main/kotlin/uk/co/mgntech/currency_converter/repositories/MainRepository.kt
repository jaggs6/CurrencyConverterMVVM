package uk.co.mgntech.currency_converter.repositories

import io.reactivex.disposables.Disposable
import uk.co.mgntech.currency_converter.requests.RatesApiClient

class MainRepository(private val apiClient: RatesApiClient) {
    companion object {
        val instance = MainRepository(RatesApiClient.instance)
    }

    fun errorLoading() = apiClient.errorLoading

    fun currencyRates() = apiClient.currencyRates

    fun ratesApi(baseCurrency: String): Disposable {
        return apiClient.getRates(baseCurrency)
    }
}
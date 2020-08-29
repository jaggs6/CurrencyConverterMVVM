package uk.co.mgntech.currency_converter.requests

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query
import uk.co.mgntech.currency_converter.models.CurrencyRate

interface RatesApi {

    companion object {
        private const val ENDPOINT_RATES = "/api/android/latest"
        private const val BASE_CURRENCY = "base"
    }

    @GET(ENDPOINT_RATES)
    fun getRates(@Query(BASE_CURRENCY) baseCurrency: String): Flowable<CurrencyRate>
}

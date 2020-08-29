package uk.co.mgntech.currency_converter.requests

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {

    companion object {
        private val retrofitBuilder =
            Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

        private val retrofit = retrofitBuilder.build()

        val ratesApi: RatesApi = retrofit.create(RatesApi::class.java)
    }
}

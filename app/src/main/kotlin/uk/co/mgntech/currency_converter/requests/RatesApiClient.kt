package uk.co.mgntech.currency_converter.requests

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import uk.co.mgntech.currency_converter.models.CurrencyView

class RatesApiClient(private val ratesApi: RatesApi) {
    companion object {
        val instance = RatesApiClient(ServiceGenerator.ratesApi)
        private const val TAG = "RatesApiClient"
    }

    val currencyRates = MutableLiveData<MutableList<CurrencyView>>()
    val errorLoading = MutableLiveData<Boolean>()

    fun getRates(baseCurrency: String): Disposable {
        val ratesFlowable = ratesApi.getRates(baseCurrency)

        return ratesFlowable
            .map { it.rates }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                updateResults(baseCurrency, it)
                Log.d(TAG, "ratesApi - $baseCurrency: $it")
            }, {
                errorLoading.postValue(true)
                Log.w(TAG, "ratesApi: ", it)
            }, {
            })
    }

    private fun updateResults(baseCurrency: String, rateMap: Map<String, Double>) {
        if (currencyRates.value.isNullOrEmpty()) {
            val mutableList = mutableListOf<CurrencyView>()
            rateMap.keys.forEach {
                val currencyView = createCurrencyView(it, MutableLiveData(rateMap[it]))
                mutableList.add(currencyView)
            }
            mutableList.sortWith { first, second -> first.currency.displayName.compareTo(second.currency.displayName) }
            mutableList.add(0, createCurrencyView(baseCurrency, MutableLiveData(1.0)))
            currencyRates.postValue(mutableList)
        } else {
            currencyRates.value?.forEach {
                it.rate.postValue(rateMap[it.currency.currencyCode])
            }
        }

        errorLoading.postValue(false)
    }

    private fun createCurrencyView(
        currencyCode: String,
        rate: MutableLiveData<Double>
    ): CurrencyView {
        return CurrencyView(
            Currency.getInstance(currencyCode),
            rate
        )
    }
}

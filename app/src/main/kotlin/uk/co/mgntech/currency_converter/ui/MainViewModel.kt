package uk.co.mgntech.currency_converter.ui

import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import uk.co.mgntech.currency_converter.models.CurrencyView
import uk.co.mgntech.currency_converter.repositories.MainRepository

class MainViewModel : ViewModel() {

    private val _repository = MainRepository.instance

    private lateinit var baseCurrency: CurrencyView
    private var repositoryDisposable: Disposable? = null
    private var intervalDisposable: Disposable? = null

    private fun refreshRates() {
        repositoryDisposable = _repository.ratesApi(baseCurrency)
    }

    fun errorLoading() = _repository.errorLoading()

    fun rates() = _repository.currencyRates()

    fun observeRates(baseCurrency: CurrencyView) {
        this.baseCurrency = baseCurrency
        intervalDisposable?.dispose()
        repositoryDisposable?.dispose()
        intervalDisposable = Observable
            .interval(0, 1, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                refreshRates()
            }
    }

    fun cancel() {
        intervalDisposable?.dispose()
        repositoryDisposable?.dispose()
    }
}

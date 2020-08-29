package uk.co.mgntech.currency_converter.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import uk.co.mgntech.currency_converter.repositories.MainRepository

class MainViewModel : ViewModel() {

    private val _repository = MainRepository.instance
    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = Runnable {
        disposable.dispose()
        refreshRates()
    }
    private lateinit var baseCurrency: String
    private lateinit var disposable: Disposable

    private fun refreshRates() {
        disposable = _repository.ratesApi(baseCurrency)
        handler.postDelayed(refreshRunnable, 1000)
    }

    fun errorLoading() = _repository.errorLoading()

    fun rates() = _repository.currencyRates()

    fun observeRates(baseCurrency: String) {
        this.baseCurrency = baseCurrency
        refreshRates()
    }

    fun cancel() {
        disposable.dispose()
        handler.removeCallbacks(refreshRunnable)
    }
}

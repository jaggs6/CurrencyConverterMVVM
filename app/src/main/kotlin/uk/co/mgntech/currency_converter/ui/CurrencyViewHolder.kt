package uk.co.mgntech.currency_converter.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.GenericTransitionOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.text.DecimalFormat
import java.util.Locale
import uk.co.mgntech.currency_converter.R
import uk.co.mgntech.currency_converter.models.CurrencyView
import uk.co.mgntech.currency_converter.utils.country
import uk.co.mgntech.currency_converter.utils.hideKeyboard
import uk.co.mgntech.currency_converter.utils.toEditable

class CurrencyViewHolder(
    private val row: View,
    private val viewLifecycleOwner: LifecycleOwner,
    private val baseCurrency: MutableLiveData<CurrencyView>,
    private val baseAmount: MutableLiveData<Double>
) : RecyclerView.ViewHolder(row), TextWatcher, TextView.OnEditorActionListener {

    companion object {
        private val CURRENCY_VALUE_FORMATTER: DecimalFormat = DecimalFormat("#.##")
    }

    private lateinit var currencyView: CurrencyView
    private val titleText = row.findViewById<TextView>(R.id.titleView)
    private val subtitleText = row.findViewById<TextView>(R.id.subtitleView)
    private val logoImage = row.findViewById<ImageView>(R.id.logoView)
    private val rateEditText = row.findViewById<EditText>(R.id.valueView)

    fun bind(
        currencyView: CurrencyView
    ) {
        this.currencyView = currencyView
        titleText.text = currencyView.currency.displayName
        subtitleText.text = currencyView.currency.currencyCode
        Glide.with(row)
            .load(
                "https://flagcdn.com/h120/${
                    currencyView.currency.country().toLowerCase(Locale.ROOT)
                }.webp"
            )
            .transition(GenericTransitionOptions.with(android.R.anim.fade_in))
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(logoImage)

        rateEditText.addTextChangedListener(this)
        rateEditText.setOnEditorActionListener(this)
        if (baseCurrency.value == currencyView) {
            baseAmount.value?.let { updateAmountBox(it, 1.0) }
        }

        baseAmount.observe(viewLifecycleOwner, {
            it?.let {
                if (baseCurrency.value != currencyView) {
                    currencyView.rate.value?.let { rate ->
                        updateAmountBox(it, rate)
                    }
                }
            }
        })

        currencyView.rate.observe(viewLifecycleOwner, {
            it?.let {
                if (baseCurrency.value != currencyView) {
                    baseAmount.value?.let { amount ->
                        updateAmountBox(amount, it)
                    }
                }
            }
        })

        baseCurrency.observe(viewLifecycleOwner, {
            rateEditText.isEnabled = it == currencyView
        })
    }

    private fun updateAmountBox(amount: Double, rate: Double) {
        rateEditText.text = CURRENCY_VALUE_FORMATTER.format(amount * rate).toEditable()
    }

    override fun afterTextChanged(editable: Editable) {
        if (currencyView == baseCurrency.value) {
            if (editable.toString().isEmpty()) {
                baseAmount.postValue(0.0)
            } else {
                baseAmount.postValue(editable.toString().toDouble())
            }
        }
    }

    override fun onEditorAction(view: TextView, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
            keyEvent == null ||
            keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
        ) {
            rateEditText.clearFocus()
            view.hideKeyboard()
            return true
        }
        return false
    }

    override fun beforeTextChanged(_1: CharSequence?, _2: Int, _3: Int, _4: Int) {}

    override fun onTextChanged(_1: CharSequence?, _2: Int, _3: Int, _4: Int) {}


}

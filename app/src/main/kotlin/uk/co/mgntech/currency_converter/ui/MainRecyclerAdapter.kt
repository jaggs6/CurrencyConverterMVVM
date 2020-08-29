package uk.co.mgntech.currency_converter.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import uk.co.mgntech.currency_converter.R
import uk.co.mgntech.currency_converter.models.CurrencyView

class MainRecyclerAdapter(private val viewLifecycleOwner: LifecycleOwner) :
    RecyclerView.Adapter<CurrencyViewHolder>(), View.OnClickListener {

    var list: MutableList<CurrencyView> = mutableListOf()
        set(value) {
            field = value
            baseCurrency.postValue(field[0])
            notifyDataSetChanged()
        }

    private val baseAmount: MutableLiveData<Double> = MutableLiveData(100.0)
    val baseCurrency: MutableLiveData<CurrencyView> = MutableLiveData()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view, viewLifecycleOwner, baseCurrency, baseAmount)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.tag = list[position]
        holder.itemView.setOnClickListener(this)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onClick(view: View) {
        val position = list.indexOf(view.tag)
        list.removeAt(position)
        list.add(0, view.tag as CurrencyView)
        baseCurrency.postValue(view.tag as CurrencyView)
        val rateBox = view.findViewById<EditText>(R.id.valueView)
        baseAmount.postValue(rateBox.text.toString().toDouble())
        notifyItemMoved(position, 0)
    }

    fun removeItem(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}

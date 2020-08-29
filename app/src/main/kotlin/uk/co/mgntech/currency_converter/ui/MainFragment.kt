package uk.co.mgntech.currency_converter.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.main_fragment.error
import kotlinx.android.synthetic.main.main_fragment.rv_main
import uk.co.mgntech.currency_converter.R
import uk.co.mgntech.currency_converter.requests.RatesApiClient

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: MainRecyclerAdapter
    private lateinit var disposable: Disposable

    private val handler = Handler(Looper.getMainLooper())
    private val refreshRunnable = Runnable {
        disposable.dispose()
        refreshRates()
    }

    private val itemTouchHelperCallback: ItemTouchHelper.SimpleCallback =
        object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = recyclerAdapter.list.indexOf(viewHolder.itemView.tag)
                recyclerAdapter.removeItem(position)
            }
        }

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        error.isVisible = false

        initRecyclerView()

        RatesApiClient.instance.apply {
            currencyRates.observe(this@MainFragment, {
                recyclerAdapter.list = it
            })
            errorLoading.observe(this@MainFragment, {
                error.isVisible = it
                rv_main.isVisible = !it
            })
        }
        refreshRates()
    }

    private fun refreshRates(forceRefresh: Boolean = false) {
        val baseCurrency = recyclerAdapter.baseCurrency.value?.currency?.currencyCode ?: "EUR"
        RatesApiClient.instance.apply {
            disposable =
                if (forceRefresh) forceRefreshRates(baseCurrency) else getRates(baseCurrency)
        }
        handler.postDelayed(refreshRunnable, 1000)
    }

    override fun onResume() {
        super.onResume()
        refreshRates()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
        disposable.dispose()
    }

    private fun initRecyclerView() {
        recyclerAdapter = MainRecyclerAdapter(viewLifecycleOwner)
        rv_main.adapter = recyclerAdapter
        rv_main.layoutManager = LinearLayoutManager(context)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_main)
    }

    override fun onAttachFragment(childFragment: Fragment) {
        super.onAttachFragment(childFragment)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }
}

package uk.co.mgntech.currency_converter.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_fragment.error
import kotlinx.android.synthetic.main.main_fragment.rv_main
import uk.co.mgntech.currency_converter.R
import uk.co.mgntech.currency_converter.models.CurrencyView

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var recyclerAdapter: MainRecyclerAdapter

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        recyclerAdapter.baseCurrency.observe(viewLifecycleOwner, {
            observeRates(it)
        })
        viewModel.apply {
            rates().observe(viewLifecycleOwner, {
                recyclerAdapter.list = it
            })
            errorLoading().observe(viewLifecycleOwner, {
                error.isVisible = it
                rv_main.isVisible = !it
            })
        }
    }

    private fun startObserving() {
        recyclerAdapter.baseCurrency.value?.let { observeRates(it) }
    }

    private fun observeRates(baseCurrencyView: CurrencyView) {
        viewModel.observeRates(baseCurrencyView)
    }

    override fun onResume() {
        super.onResume()
        startObserving()
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter.baseCurrency.removeObservers(viewLifecycleOwner)
        viewModel.apply {
            rates().removeObservers(viewLifecycleOwner)
            errorLoading().removeObservers(viewLifecycleOwner)
        }
    }

    private fun initRecyclerView() {
        recyclerAdapter = MainRecyclerAdapter(viewLifecycleOwner)
        rv_main.adapter = recyclerAdapter
        rv_main.layoutManager = LinearLayoutManager(context)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rv_main)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    companion object {
        fun newInstance() = MainFragment()
    }
}

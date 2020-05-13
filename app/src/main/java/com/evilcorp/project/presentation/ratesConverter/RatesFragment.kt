package com.evilcorp.project.presentation.ratesConverter

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.SimpleItemAnimator
import com.evilcorp.project.R
import com.evilcorp.project.helpers.injectRatesFragment
import com.evilcorp.project.presentation.RatesViewModelFactory
import com.evilcorp.project.presentation.model.CurrencyRateModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_converter.*
import timber.log.Timber
import javax.inject.Inject

class RatesFragment : Fragment(R.layout.fragment_converter) {

    @Inject
    lateinit var vmFactory: RatesViewModelFactory

    @Inject
    lateinit var rateDecorator: RateDecorator

    private lateinit var viewModel: RatesViewModel
    private lateinit var ratesAdapter: RatesListAdapter
    private var currencyDisposable: Disposable? = null

    override fun onAttach(context: Context) {
        context.injectRatesFragment(this)
        viewModel = ViewModelProvider(this, vmFactory).get(RatesViewModel::class.java)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ratesAdapter = RatesListAdapter(
            this::onSelectedCurrencyUpdate,
            this::onSelectedValueUpdate,
            rateDecorator
        )

        rv_rates_list.apply {
            adapter = ratesAdapter
            (itemAnimator  as? SimpleItemAnimator)?.supportsChangeAnimations = false
            setHasFixedSize(true)
        }

        srl_refresh_holder.setOnRefreshListener { startCurrencyRefresh() }
    }

    private fun onSelectedCurrencyUpdate(currency: CurrencyRateModel) {
        viewModel.onCurrencyUpdate(currency)
    }

    private fun onSelectedValueUpdate(value: Float) {
        viewModel.onSelectedValueUpdate(value)
    }

    override fun onStart() {
        super.onStart()
        startCurrencyRefresh()
    }

    override fun onStop() {
        super.onStop()
        currencyDisposable?.dispose()
    }

    private fun startCurrencyRefresh() {
        currencyDisposable?.dispose()
        currencyDisposable = viewModel.getCurrencyList()
            .doOnSubscribe { setLoadingState(true) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext { setLoadingState(false) }
            .subscribe(ratesAdapter::submitList) {
                setErrorState()
                Timber.w(it)
            }
    }

    private fun setLoadingState(loading: Boolean) {
        srl_refresh_holder.isRefreshing = loading
        tv_error.isVisible = false
    }

    private fun setErrorState() {
        tv_error.isVisible = true
        srl_refresh_holder.isRefreshing = false
    }
}
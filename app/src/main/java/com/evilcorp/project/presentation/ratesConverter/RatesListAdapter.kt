package com.evilcorp.project.presentation.ratesConverter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.evilcorp.project.R
import com.evilcorp.project.presentation.model.CurrencyRateModel
import kotlinx.android.synthetic.main.layout_rates_list_item.view.*

class RatesListAdapter(
    private val onCurrencySelected: (CurrencyRateModel) -> Unit,
    private val onValueChange: (Float) -> Unit,
    private val rateDecorator: RateDecorator
): ListAdapter<CurrencyRateModel, RatesListAdapter.RatesVH>(
    CurrencyDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesVH {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_rates_list_item, parent, false)
        return RatesVH(view)
    }

    override fun onBindViewHolder(holder: RatesVH, position: Int) {
        val dataItem = getItem(position)
        holder.bind(dataItem)
    }

    inner class RatesVH(view: View): RecyclerView.ViewHolder(view) {
        private val iconImageView = view.iv_currency_icon
        private val titleTextView = view.tv_currency_name
        private val descriptionTextView = view.tv_currency_description
        private val rateEditText = view.et_currency_rate
        private var touchKeeper = view.v_touch_keeper

        private val isSelectedItem: Boolean get() = adapterPosition == 0
        private val rateEditListener = ItemRateChangeListener()

        private var allowValueUpdate = true

        init {
            rateEditText.addTextChangedListener(rateEditListener)
        }

        fun bind(item: CurrencyRateModel) {
            titleTextView.text = item.currencyCode
            descriptionTextView.text = item.currencyDescription
            iconImageView.setImageResource(item.currencyImageRes)

            setupSelectionHandler(item)
            setupRateEdit(item.value)
        }

        private fun setupSelectionHandler(item: CurrencyRateModel) {
            touchKeeper.setOnClickListener {
                allowValueUpdate = true
                onCurrencySelected(item)
            }
            touchKeeper.isVisible = !isSelectedItem
        }

        private fun setupRateEdit(rate: Float) {
            if (isSelectedItem) {
                rateEditListener.updateTo(onValueChange::invoke)
            } else {
                allowValueUpdate = true
                rateEditListener.stopUpdate()
            }

            if (allowValueUpdate) {
                val rateDecorated = rateDecorator.getRateDecorated(rate)
                rateEditText.setText(rateDecorated, TextView.BufferType.EDITABLE)
                rateEditText.setSelection(rateDecorated.length)
                if (isSelectedItem) allowValueUpdate = false
            }

            rateEditText.isEnabled = isSelectedItem
        }
    }

    private class CurrencyDiffCallback: DiffUtil.ItemCallback<CurrencyRateModel>() {

        override fun areItemsTheSame(oldItem: CurrencyRateModel, newItem: CurrencyRateModel): Boolean {
            return oldItem.currencyCode == newItem.currencyCode
        }

        override fun areContentsTheSame(oldItem: CurrencyRateModel, newItem: CurrencyRateModel): Boolean {
            return oldItem.value == newItem.value
        }
    }

    private class ItemRateChangeListener: TextWatcher {
        private var updateListener: ((Float) -> Unit)? = null

        fun updateTo(updateListener: (Float) -> Unit) {
            this.updateListener = updateListener
        }

        fun stopUpdate() {
            updateListener = null
        }

        override fun afterTextChanged(s: Editable?) {}

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateListener?.let {
                val floatValue = s.toString().toFloatOrNull() ?: 0f
                it(floatValue)
            }
        }
    }
}
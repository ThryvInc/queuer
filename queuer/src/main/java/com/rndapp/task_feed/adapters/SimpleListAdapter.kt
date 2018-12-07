package com.rndapp.task_feed.adapters

import com.rndapp.task_feed.adapters.items.HighlightableItem
import com.rndapp.task_feed.adapters.items.HighlightableSimpleViewModel
import com.rndapp.task_feed.adapters.items.SimpleItem
import com.rndapp.task_feed.adapters.items.SimpleViewModel
import com.rndapp.task_feed.views.HighlightableViewHolder
import com.thryvinc.thux.adapters.ModelRecyclerViewAdapter
import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface

open class SimpleListAdapter<T>(array: List<T>, val toViewModel: (T) -> SimpleViewModel, val listener: (T) -> Unit):
        ModelRecyclerViewAdapter(arrayToItems<T>(array, toViewModel, listener)) {
    companion object {
        fun <T> arrayToItems(array: List<T>, toViewModel: (T) -> SimpleViewModel, listener: (T) -> Unit): List<RecyclerItemViewModelInterface> {
            return array.map(toViewModel).map { viewModel ->
                SimpleItem(viewModel) { listener.invoke(array[it.adapterPosition]) }
            }
        }
    }

    fun updateArray(array: List<T>) {
        super.itemViewModels = arrayToItems(array, toViewModel, listener)
        notifyDataSetChanged()
    }
}

open class HighlightableListAdapter<T>(array: List<T>, toViewModel: (T) -> HighlightableSimpleViewModel, listener: ((T, Boolean) -> Unit)?):
        ModelRecyclerViewAdapter(array.map(toViewModel).map { viewModel ->
            HighlightableItem(viewModel) {
                if (it is HighlightableViewHolder) {
                    it.toggleHighlight()
                    viewModel.isHighlighted = it.isHighlighted
                    listener?.invoke(array[it.adapterPosition], it.isHighlighted)
                }
            }
        })

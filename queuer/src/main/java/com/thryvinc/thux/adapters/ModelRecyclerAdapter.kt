package com.thryvinc.thux.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.thryvinc.thux.models.OnBoundProvider

interface RecyclerItemViewModelInterface {
    fun configureHolder(holder: RecyclerView.ViewHolder)
    fun newViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun viewType(): Int
}

abstract class RecyclerItemViewModel<T>(val model: T): RecyclerItemViewModelInterface

interface ModelConfigurable<T> {
    fun configure(model: T)
}

abstract class ModelViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView), ModelConfigurable<T> {
}

abstract class LayoutIdRecyclerItemViewModel(val layoutId: Int): RecyclerItemViewModelInterface {
    override fun viewType(): Int = layoutId

    override fun newViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(layoutId, parent, false)
        return viewHolderWithView(view)
    }

    abstract fun viewHolderWithView(view: View): RecyclerView.ViewHolder
}

abstract class ModelLayoutIdRecyclerItemViewModel<T>(val model: T, layoutId: Int): LayoutIdRecyclerItemViewModel(layoutId) {
    override fun viewHolderWithView(view: View): RecyclerView.ViewHolder = modelViewHolderWithView(view)
    abstract fun modelViewHolderWithView(view: View): ModelViewHolder<T>
}

open class ModelRecyclerViewAdapter(var itemViewModels: List<RecyclerItemViewModelInterface>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun getItemCount(): Int {
        return itemViewModels.size
    }

    override fun getItemViewType(position: Int): Int {
        return itemViewModels[position].viewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return itemViewModels.first { it.viewType() == viewType }.newViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemViewModel = itemViewModels[position]
        itemViewModel.configureHolder(holder)
    }
}

abstract class OnBoundAdapter(itemViewModels: List<RecyclerItemViewModelInterface>): ModelRecyclerViewAdapter(itemViewModels), OnBoundProvider

open class OnBoundModelRecyclerViewAdapter(itemViewModels: List<RecyclerItemViewModelInterface>): OnBoundAdapter(itemViewModels) {
    var _onBound: ((Int, Int) -> Unit)? = null
    override var onBound: ((Int, Int) -> Unit)?
        get() = _onBound
        set(value) {
            _onBound = value
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val onBound = _onBound
        if (onBound != null) {
            onBound(position, itemViewModels.size)
        }
    }

}

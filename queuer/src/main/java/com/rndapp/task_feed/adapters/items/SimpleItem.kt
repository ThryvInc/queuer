package com.rndapp.task_feed.adapters.items

import android.support.v7.widget.RecyclerView
import android.view.View
import com.rndapp.task_feed.R
import com.rndapp.task_feed.views.HighlightableViewHolder
import com.rndapp.task_feed.views.SimpleViewHolder
import com.thryvinc.thux.adapters.LayoutIdRecyclerItemViewModel

data class SimpleViewModel(val leftText: String, val rightText: String)
data class HighlightableSimpleViewModel(val leftText: String, val rightText: String, var isHighlighted: Boolean = false)

open class SimpleItem(val model: SimpleViewModel, val listener: (RecyclerView.ViewHolder) -> Unit):
        LayoutIdRecyclerItemViewModel(R.layout.item) {
    override fun configureHolder(holder: RecyclerView.ViewHolder) {
        if (holder is SimpleViewHolder) {
            holder.setText(model.leftText)
            holder.setRightText(model.rightText)
        }
    }

    override fun viewHolderWithView(view: View): RecyclerView.ViewHolder {
        return SimpleViewHolder(view, listener)
    }
}

class HighlightableItem(val model: HighlightableSimpleViewModel, val listener: (RecyclerView.ViewHolder) -> Unit):
        LayoutIdRecyclerItemViewModel(R.layout.item) {

    override fun configureHolder(holder: RecyclerView.ViewHolder) {
        if (holder is SimpleViewHolder) {
            holder.setText(model.leftText)
            holder.setRightText(model.rightText)
        }

        if (holder is HighlightableViewHolder) {
            if (holder.isHighlighted != model.isHighlighted) {
                holder.toggleHighlight()
            }
        }
    }

    override fun viewHolderWithView(view: View): RecyclerView.ViewHolder {
        return HighlightableViewHolder(view, listener)
    }
}

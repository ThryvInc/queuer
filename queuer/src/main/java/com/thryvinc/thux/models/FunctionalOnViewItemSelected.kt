package com.thryvinc.thux.models

import com.thryvinc.thux.adapters.RecyclerItemViewModelInterface
import com.thryvinc.thux.interfaces.OnViewItemSelected

class FunctionalOnViewItemSelected(var onSelect: (RecyclerItemViewModelInterface) -> Unit): OnViewItemSelected {
    override fun onItemSelected(item: RecyclerItemViewModelInterface) {
        onSelect(item)
    }
}
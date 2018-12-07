package com.thryvinc.thux.view_models

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.thryvinc.thux.adapters.*
import com.thryvinc.thux.injectBefore
import com.thryvinc.thux.into
import com.thryvinc.thux.intoSecond
import com.thryvinc.thux.map
import com.thryvinc.thux.models.*
import com.thryvinc.thux.network.*

open class ModelCallViewModel<T>(call: NetworkCall<T>?): ViewModel() {
    protected val modelLiveData: MutableLiveData<T?> = MutableLiveData()
    open var call: NetworkCall<T>? = null
        set(value) {
            field = value
            setupCallToData()
        }

    init {
        this.call = call
    }

    open fun setupCallToData() {
        val listener = call?.listener
        if (listener != null && listener != this::setData) {
            call?.listener = this::setData injectBefore listener
        }
    }

    open fun setData(data: T?) {
        if (data != null) {
            modelLiveData.value = data
        }
    }
}

open class RecyclerItemCallViewModel<T>(call: NetworkCall<List<T>>?,
                                        var modelToItem: (T) -> RecyclerItemViewModelInterface,
                                        var onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null):
        ModelCallViewModel<List<T>>(call) {
    protected val itemsLiveData: MutableLiveData<List<RecyclerItemViewModelInterface>> = MutableLiveData()

    init {
        setupOnItems()
    }

    fun setupOnItems() {
        modelLiveData.observeForever { models: List<T>? ->
            if (models != null) {
                itemsLiveData.value = models into (modelToItem intoSecond ::map)
            }
        }
        itemsLiveData.observeForever {
            val onItems = onItems
            if (onItems != null && it != null) {
                onItems(it)
            }
        }
    }
}

open class RecyclerItemAdapterCallViewModel<T>(call: NetworkCall<List<T>>?,
                                               modelToItem: (T) -> RecyclerItemViewModelInterface,
                                               onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                               val adapter: ModelRecyclerViewAdapter):
        RecyclerItemCallViewModel<T>(call, modelToItem, onItems) {

    open fun notifyAdapter() {
        adapter.notifyDataSetChanged()
    }
}

open class RefreshableRecyclerItemAdapterViewModel<T>(call: NetworkCall<List<T>>?,
                                                      modelToItem: (T) -> RecyclerItemViewModelInterface,
                                                      onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                                      adapter: ModelRecyclerViewAdapter,
                                                      var refreshManager: Refreshable?):
        RecyclerItemAdapterCallViewModel<T>(call, modelToItem, onItems, adapter) {

    init {
        super.onItems = ::setAdapterModels injectBefore ::notifyAdapter
    }

    open fun setAdapterModels(items: List<RecyclerItemViewModelInterface>?) {
        if (items != null) {
            if (refreshManager?.isRefreshing == true) {
                adapter.itemViewModels = items
            } else if (refreshManager?.isRefreshing == false) {
                val arrayList = ArrayList(adapter.itemViewModels)
                arrayList.addAll(items)
                adapter.itemViewModels = arrayList
            }
        }
    }

    open fun resetRefreshManager() {
        refreshManager?.isRefreshing = null
    }
}

open class PagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                      modelToItem: (T) -> RecyclerItemViewModelInterface,
                                      onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                      onBoundAdapter: OnBoundAdapter,
                                      var callManager: PageableCallManager<List<T>> = PageableCallManager(call)):
        RefreshableRecyclerItemAdapterViewModel<T>(call, modelToItem, onItems, onBoundAdapter, callManager) {
    var pageSize = 20
    var pageTrigger = 5
    var shouldLoadMore: (Int, Int) -> Boolean = { position: Int, outOf: Int ->
        position == outOf - pageTrigger && outOf % pageSize == 0
    }

    init {
        onBoundAdapter.onBound = { position: Int, outOf: Int ->
            if (shouldLoadMore(position, outOf)) {
                callManager.nextPage()
            }
        }
    }
}

open class SimplePagingRecyclerViewModel<T>(call: PagedCall<List<T>>?,
                                            modelToItem: (T) -> RecyclerItemViewModelInterface,
                                            onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                            onBoundModelRecyclerAdapter: OnBoundAdapter):
        PagingRecyclerViewModel<T>(call, modelToItem, onItems, onBoundModelRecyclerAdapter) {

    init {
        setupRefreshManager()
    }

    fun setupRefreshManager() {
        val listener = call?.listener
        if (listener != null) {
            call?.listener = listener injectBefore ::resetRefreshManager
        }
    }
}

open class ModelPagingRecyclerViewModel<T>(call: UrlParameteredIndexCall<T>?,
                                           modelToItem: (T) -> RecyclerItemViewModelInterface,
                                           onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                           onBoundAdapter: OnBoundAdapter,
                                           var callManager: ModelPageable<T>? = null):
        RefreshableRecyclerItemAdapterViewModel<T>(call, modelToItem, onItems, onBoundAdapter, callManager) {
    var shouldLoadMore: (Int) -> Boolean = { false }
    var modelThatDeterminesNextPage: (() -> T?)? = null

    init {
        onBoundAdapter.onBound = { position: Int, _ ->
            if (shouldLoadMore(position)) {
                val lastModel = modelThatDeterminesNextPage?.invoke()
                callManager?.nextPage(lastModel)
            }
        }
    }
}

open class SimpleModelPagingRecyclerViewModel<T>(call: UrlParameteredIndexCall<T>?,
                                                 modelToItem: (T) -> RecyclerItemViewModelInterface,
                                                 onItems: ((List<RecyclerItemViewModelInterface>) -> Unit)? = null,
                                                 onBoundModelRecyclerAdapter: OnBoundModelRecyclerViewAdapter,
                                                 callManager: ModelPageable<T>? = null):
        ModelPagingRecyclerViewModel<T>(call, modelToItem, onItems, onBoundModelRecyclerAdapter, callManager) {

    init {
        setupRefreshManager()
    }

    fun setupRefreshManager() {
        val listener = call?.listener
        if (listener != null) {
            call?.listener = (listener injectBefore ::resetRefreshManager)
        }
    }
}

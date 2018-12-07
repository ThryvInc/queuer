package com.thryvinc.thux.models

interface OnBoundProvider {
    var onBound: ((Int, Int) -> Unit)?
}

interface OnBoundModelProvider<T> {
    var onBoundModel: ((T, Int, Int) -> Unit)?
}

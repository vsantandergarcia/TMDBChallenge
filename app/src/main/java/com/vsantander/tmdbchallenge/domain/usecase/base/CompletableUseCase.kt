package com.vsantander.tmdbchallenge.domain.usecase.base

import io.reactivex.Completable

abstract class CompletableUseCase<in T> :
        RxUseCase<T, Completable>()
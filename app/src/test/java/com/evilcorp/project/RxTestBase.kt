package com.evilcorp.project

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.mockito.Mockito

open class RxTestBase {
    val disposables = CompositeDisposable()
    var disposer: Disposable
        get() = disposables
        set(value) { disposables.add(value) }

    @Before
    fun overrideSchedulers() {
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @After
    fun resetSchedulers() {
        disposables.dispose()
        RxAndroidPlugins.reset()
        RxJavaPlugins.reset()
    }

    fun <T> any(): T = Mockito.any<T>()
}
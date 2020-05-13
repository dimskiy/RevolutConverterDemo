package com.evilcorp.project

import android.app.Application
import com.evilcorp.project.di.AppComponent
import com.evilcorp.project.di.AppModule
import com.evilcorp.project.di.DaggerAppComponent

class App: Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        super.onCreate()
    }
}
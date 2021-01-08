package com.example.pokedata

import android.app.Application
import android.content.res.Resources

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        res = resources
    }

    companion object {
        lateinit var instance: App
            private set
        private lateinit var res: Resources
        fun getRes(): Resources {
            return res
        }
    }
}
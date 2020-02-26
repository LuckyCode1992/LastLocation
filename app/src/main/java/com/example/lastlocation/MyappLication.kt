package com.example.lastlocation

import android.app.Application

class MyappLication : Application() {
    companion object{
        var appContext:Application?=null
    }
    override fun onCreate() {
        super.onCreate()
        appContext = this
    }
}
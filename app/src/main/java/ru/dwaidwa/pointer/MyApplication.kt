package ru.dwaidwa.pointer

import android.app.Application

class MyApplication : Application() {
    // Наследуемся от Application, DataStore будет использовать Application Context
    override fun onCreate() {
        super.onCreate()
        // DataStore не требует явной инициализации, но Application - это правильное место
        // для инициализации других компонентов.
    }
}
package com.example.newasteriodapp.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory (private val app: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (isMainViewModelClass(modelClass)) {
            return developMainViewModel() as T
        }
        throw IllegalArgumentException("cannot retrieve viewmodel")
    }

    private fun isMainViewModelClass(modelClass: Class<*>): Boolean {
        return modelClass.isAssignableFrom(MainViewModel::class.java)
    }

    private fun developMainViewModel(): MainViewModel {
        return MainViewModel(app)
    }
}

package com.jc.sistema.UI.Menu.ui.sales

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Muy Pronto..."
    }
    val text: LiveData<String> = _text
}
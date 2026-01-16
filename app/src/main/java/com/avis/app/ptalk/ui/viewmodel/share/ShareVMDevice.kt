package com.avis.app.ptalk.ui.viewmodel.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.avis.app.ptalk.domain.model.Device

class ShareVMDevice : ViewModel() {
    private val _device = MutableLiveData<Device?>()
    val device: LiveData<Device?> = _device

    fun setDevice(device: Device) {
        _device.value = device
    }

    fun clearDevice() {
        _device.value = null
    }
}
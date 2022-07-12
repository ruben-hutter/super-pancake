package com.example.wifi_dir_test

import android.net.wifi.p2p.WifiP2pDevice
import androidx.fragment.app.ListFragment

class DeviceListFragment : ListFragment() {

    private val peers = mutableListOf<WifiP2pDevice>()
    private lateinit var device: WifiP2pDevice

    fun updateThisDevice(device: WifiP2pDevice) {
        this.device = device
    }
}
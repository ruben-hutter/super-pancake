package com.example.wifi_dir_test

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pConfig
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat


class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: MainActivity) : BroadcastReceiver() {

    companion object {
        private const val TAG = "WiFiDirBrdcstRcvr"
    }

    private val peers = mutableListOf<WifiP2pDevice>()
    private var peersNames = emptyArray<String?>()
    private lateinit var myDevice: WifiP2pDevice

    private val peerListListener = WifiP2pManager.PeerListListener { peerList ->
        val refreshedPeers = peerList.deviceList
        if (refreshedPeers != peers) {
            peers.clear()
            peers.addAll(refreshedPeers)

            // If an AdapterView is backed by this data, notify it
            // of the change. For instance, if you have a ListView of
            // available peers, trigger an update.
            peersNames = arrayOfNulls<String?>(refreshedPeers.size)
            var i = 0;
            for (peer in refreshedPeers) {
                peersNames[i++] = peer.deviceName
            }
            val listAdapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, peersNames)
            activity.setListAdapter(listAdapter)
            //(listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()
            listAdapter.notifyDataSetChanged()

            // Perform any other updates needed based on the new list of
            // peers connected to the Wi-Fi P2P network.
        }

        if (peers.isEmpty()) {
            Log.d(TAG, "No devices found")
            return@PeerListListener
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when(intent.action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Determine if Wifi P2P mode is enabled or not, alert
                // the Activity.
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                activity.isWifiP2pEnabled = state == WifiP2pManager.WIFI_P2P_STATE_ENABLED
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Request available peers from the wifi p2p manager. This is an
                // asynchronous call and the calling activity is notified with a
                // callback on PeerListListener.onPeersAvailable()
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    Log.d(TAG, "Failed to refresh")
                    return
                }
                manager.requestPeers(channel, peerListListener)
                Log.d(TAG, "P2P peers changed")
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {

                // Connection state changed! We should probably do something about
                // that.

            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                (activity.supportFragmentManager.findFragmentById(R.id.frag_list)
                    .apply {
                        updateThisDevice(
                            intent.getParcelableExtra<WifiP2pDevice>(
                                WifiP2pManager.EXTRA_WIFI_P2P_DEVICE
                            ) as WifiP2pDevice
                        )
                    })
            }
        }
    }

    private fun updateThisDevice(device: WifiP2pDevice) {
        myDevice = device
    }

    override fun connect(position: Int) {
        val device = peers[position]

        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
            wps.setup = WpsInfo.PBC
        }

        manager.connect(channel, config, object : WifiP2pManager.ActionListener {

            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver notifies us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    activity,
                    "Connect failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
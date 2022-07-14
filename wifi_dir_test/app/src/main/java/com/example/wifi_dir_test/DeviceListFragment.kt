package com.example.wifi_dir_test

import android.content.Context
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.ListFragment


class DeviceListFragment : ListFragment(), PeerListListener {

    companion object {
        private const val TAG = "DeviceListFragment"
    }

    private val peerList = ArrayList<WifiP2pDevice>()

    var device: WifiP2pDevice? = null
        private set

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.listAdapter = WiFiPeerListAdapter(requireActivity(), R.layout.row_devices, peerList)
    }

    fun updateThisDevice(device: WifiP2pDevice) {
        this.device = device
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.device_list, null)
    }

    private fun getDeviceStatus(deviceStatus: Int): String {
        Log.d(TAG, "Peer status : $deviceStatus")
        return when (deviceStatus) {
            WifiP2pDevice.AVAILABLE -> "Available"
            WifiP2pDevice.INVITED -> "Invited"
            WifiP2pDevice.CONNECTED -> "Connected"
            WifiP2pDevice.FAILED -> "Failed"
            WifiP2pDevice.UNAVAILABLE -> "Unavailable"
            else -> "Unknown"
        }
    }

    // initiate connection with the peer, apparently
    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        val device = listAdapter!!.getItem(position) as WifiP2pDevice
        (activity as DeviceActionListener).showDetails(device)
    }

    override fun onPeersAvailable(peers: WifiP2pDeviceList) {
        peerList.clear()
        peerList.addAll(peers.deviceList)
        for (s in peers.deviceList) {
            Log.d(TAG, "onPeersAvailable, $s")
        }
        (listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()
        if (peerList.size == 0) {
            Log.d(TAG, "no devices were found")
        }
    }

    fun clearAllPeers() {
        peerList.clear()
        (listAdapter as WiFiPeerListAdapter).notifyDataSetChanged()
    }

    private inner class WiFiPeerListAdapter
    /**
     * @param context
     * @param textViewResourceId
     * @param objects
     */
        (context: Context, textViewResourceId: Int,
         private val items: List<WifiP2pDevice>) : ArrayAdapter<WifiP2pDevice>(context, textViewResourceId, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var v = convertView
            if (v == null) {
                val vi = requireActivity().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                v = vi.inflate(R.layout.row_devices, null)
            }
            val device = items[position]
            val top = v!!.findViewById<View>(R.id.device_name) as TextView
            val bottom = v.findViewById<View>(R.id.device_details) as TextView
            top.text = device.deviceName
            bottom.text = getDeviceStatus(device.status)
            Log.d(TAG, "WiFiPeerListAdapter getView")
            return v
        }
    }

    interface DeviceActionListener {

        fun showDetails(device: WifiP2pDevice)

        fun cancelDisconnect()

        fun connect(config: WifiP2pConfig)

        fun disconnect()
    }
}

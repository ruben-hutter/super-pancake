package com.example.wifi_dir_test

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    var isWifiP2pEnabled: Boolean = false

    // GUI related variables
    private lateinit var textView: TextView
    private lateinit var typeMessage: EditText
    private lateinit var sendButton: Button
    private lateinit var scanButton: Button
    private lateinit var fragList: ListView

    // event filter
    private val intentFilter = IntentFilter()

    // broadcast receiver
    private lateinit var receiver: BroadcastReceiver

    // WifiP2PManager
    private lateinit var manager: WifiP2pManager
    private lateinit var channel: WifiP2pManager.Channel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // init GUI
        initGUI()
        // init intentFilter
        initIntentFilter()
        // init WifiP2PManager
        initWifiP2PManager()
    }

    /**
     * Initialize GUI related variables.
     */
    private fun initGUI() {
        // link to GUI
        textView = findViewById(R.id.textView)
        typeMessage = findViewById(R.id.typeMessage)
        sendButton = findViewById(R.id.sendButton)
        scanButton = findViewById(R.id.scanButton)
        fragList = findViewById(R.id.frag_list)

        // set Buttons
        sendButton.setOnClickListener {
            textView.text = typeMessage.text
            Log.i(TAG, "textView: ${textView.text}")
            sendWithWiFiDirect()
            typeMessage.text = null
        }
        scanButton.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
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
                return@setOnClickListener
            }
            manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    // Code for when the discovery initiation is successful goes here.
                    // No services have actually been discovered yet, so this method
                    // can often be left blank. Code for peer discovery goes in the
                    // onReceive method, detailed below.
                    Log.i(TAG, "Discovery started")
                }

                override fun onFailure(reasonCode: Int) {
                    // Code for when the discovery initiation fails goes here.
                    // Alert the user that something went wrong.
                    Log.i(TAG, "Discovery failed")
                }
            })
        }
    }

    /**
     * Initialize IntentFilter related possible actions.
     */
    private fun initIntentFilter() {
        // Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    /**
     * Initialize WifiP2PManager instance and get a Channel object to connect
     * the app to the Wi-Fi P2P framework.
     */
    private fun initWifiP2PManager() {
        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)
    }

    /** register the BroadcastReceiver with the intent values to be matched  */
    public override fun onResume() {
        super.onResume()
        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        registerReceiver(receiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    /**
     * Set the updated list of peers to the ListView
     */
    fun setListAdapter(adapterUpdate: ListAdapter) {
        fragList.adapter = adapterUpdate
    }

    private fun sendWithWiFiDirect() {
        // TODO this method will send the typed message to the selected peer or peer group
    }
}
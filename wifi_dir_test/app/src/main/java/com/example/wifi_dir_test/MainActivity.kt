package com.example.wifi_dir_test

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {

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

        // set Button
        sendButton.setOnClickListener(this)
        scanButton.setOnClickListener(this)
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

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sendButton -> {
                textView.text = typeMessage.text
                Log.i(TAG, "textView: ${textView.text}")
                sendWithWiFiDirect()
                typeMessage.text = null
            }
            R.id.scanButton -> {
                // TODO discover new devices
            }
        }
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

    private fun sendWithWiFiDirect() {
        // TODO
    }
}
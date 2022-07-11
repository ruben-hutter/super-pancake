package com.example.wifi_dir_test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity(), View.OnClickListener {
    // GUI related variables
    private lateinit var textView: TextView
    private lateinit var typeMessage: EditText
    private lateinit var sendMessage: Button

    // event filter
    private val intentFilter = IntentFilter()

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
        sendMessage = findViewById(R.id.sendMessage)

        // set Button
        sendMessage.setOnClickListener(this)
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
            R.id.sendMessage -> {
                textView.text = typeMessage.text
                Log.i(TAG, "textView: ${textView.text}")
                sendWithWiFiDirect()
                typeMessage.text = null
            }
        }
    }

    private fun sendWithWiFiDirect() {
        // TODO
    }
}
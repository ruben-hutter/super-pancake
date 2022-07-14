package com.example.wifi_dir_test

import android.os.Handler
import android.os.Looper
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Client(hostAddress: InetAddress, private var socket: Socket) : Thread() {

    private val hostAddr: String
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    init {
        hostAddr = hostAddress.hostAddress
        socket = Socket()
    }

    override fun run() {
        socket.connect(InetSocketAddress(hostAddr, 8888), 500)
        inputStream = socket.getInputStream()
        outputStream = socket.getOutputStream()

        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
    }
}
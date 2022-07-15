package com.example.wifi_dir_test

import android.os.Handler
import android.os.Looper
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Client(
    private val hostAddress: String?,
    private var socket: Socket,
    private val activity: MainActivity
) : Thread() {

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    init {
        socket = Socket()
    }

    fun write(byteArray: ByteArray) {
        try {
            outputStream.write(byteArray)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun run() {
        try {
            socket.connect(InetSocketAddress(hostAddress, 8888), 500)
            inputStream = socket.getInputStream()
            outputStream = socket.getOutputStream()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val executor: ExecutorService = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val buffer = byteArrayOf(1024.toByte())
            var bytes: Int

            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    if (bytes > 0) {
                        handler.post {
                            val tmpMessage = String(buffer, 0, bytes)
                            activity.setMessage(tmpMessage)
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}
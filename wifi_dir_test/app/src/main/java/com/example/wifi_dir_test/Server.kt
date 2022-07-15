package com.example.wifi_dir_test

import android.os.Handler
import android.os.Looper
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Server(
    private var socket: Socket,
    private val activity: MainActivity
) : Thread() {

    private lateinit var serverSocket: ServerSocket
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
            serverSocket = ServerSocket(8888)
            socket = serverSocket.accept()
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
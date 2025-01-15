package ro.pub.cs.systems.eim.practicaltest02v2

import android.os.Bundle
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.Socket
import kotlin.concurrent.thread

class TimeDisplayActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private val serverIP = "192.168.45.25"  // IP-ul pentru mașina gazdă
    private val serverPort = 12345     // Portul folosit de serverul Python

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_display)

        tvTime = findViewById(R.id.tvTime)

        Thread {
            try {
                val socket = Socket(serverIP, serverPort)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                while (true) {
                    val time = reader.readLine()
                    runOnUiThread {
                        tvTime.text = time
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    tvTime.text = "Error: ${e.message}"
                }
            }
        }.start()
    }
}
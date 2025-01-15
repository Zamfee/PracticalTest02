package ro.pub.cs.systems.eim.practicaltest02v2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PracticalTest02v2MainActivity : AppCompatActivity() {
    private lateinit var etWord: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvDefinition: TextView
    private lateinit var btnShowTime: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etWord = findViewById(R.id.etWord)
        btnSearch = findViewById(R.id.btnSearch)
        tvDefinition = findViewById(R.id.tvDefinition)
        btnShowTime = findViewById(R.id.btnShowTime)

        btnSearch.setOnClickListener {
            val word = etWord.text.toString().trim()
            if (word.isNotEmpty()) {
                fetchDefinition(word)
            } else {
                Toast.makeText(this, "Please enter a word", Toast.LENGTH_SHORT).show()
            }
        }

        btnShowTime.setOnClickListener {
            val intent = Intent(this, TimeDisplayActivity::class.java)
            startActivity(intent)
        }

        val filter = IntentFilter("com.example.dictionary.DEFINITION_BROADCAST")
        registerReceiver(definitionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    private fun fetchDefinition(word: String) {
        Log.d("FetchDefinition", "Fetching definition for: $word")
        fetchDefinitionFromWeb(word)
    }

    private fun parseDefinition(dictionaryResponse: List<DictionaryResponse>) {
        val firstDefinition = dictionaryResponse.getOrNull(0)?.meanings?.getOrNull(0)
            ?.definitions?.getOrNull(0)?.definition

        if (firstDefinition != null) {
            Log.d("ParsedDefinition", "First Definition: $firstDefinition")

            val intent = Intent("com.example.dictionary.DEFINITION_BROADCAST").apply {
                putExtra("definition", firstDefinition)
            }
            sendBroadcast(intent)

        } else {
            Log.e("ParsedDefinition", "No definition found in the response.")
            val intent = Intent("com.example.dictionary.DEFINITION_BROADCAST").apply {
                putExtra("definition", "Definition not found")
            }
            sendBroadcast(intent)
        }
    }

    private fun fetchDefinitionFromWeb(word: String) {
        RetrofitInstance.api.getDefinition(word).enqueue(object : Callback<List<DictionaryResponse>> {
            override fun onResponse(
                call: Call<List<DictionaryResponse>>,
                response: Response<List<DictionaryResponse>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { dictionaryResponse ->
                        Log.d("ServerResponse", "Raw Response: $dictionaryResponse")
                        parseDefinition(dictionaryResponse)
                    } ?: run {
                        Log.e("ServerResponse", "Empty response from server.")
                    }
                } else {
                    Log.e("ServerError", "Error: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<DictionaryResponse>>, t: Throwable) {
                Log.e("ServerFailure", "Failed to fetch data: ${t.message}")
            }
        })
    }

    private val definitionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val definition = intent.getStringExtra("definition")
            if (definition != null) {
                tvDefinition.text = definition
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(definitionReceiver)
    }


}
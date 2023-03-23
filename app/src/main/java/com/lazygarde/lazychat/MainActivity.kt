package com.lazygarde.lazychat

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lazygarde.lazychat.databinding.ActivityMainBinding
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var messageAdapter: MessageAdapter
    private val messages = mutableListOf<Message>()
    private lateinit var binding: ActivityMainBinding


    private val json: MediaType = "application/json; charset=utf-8".toMediaType()
    val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
        onEvent()
        setContentView(binding.root)
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        recyclerView = binding.recyclerView
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        messageAdapter = MessageAdapter(messages)
        recyclerView.adapter = messageAdapter

    }


    private fun onEvent() {
        binding.sendButton.setOnClickListener {
            val message = binding.editText.text.toString().trim()
            addMessage(message, SentType.Me)
            binding.editText.setText("")
            callAPI(message)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun addMessage(message: String, sentType: SentType) {
        runOnUiThread {
            messages.add(Message(message, sentType))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }

    }

    private fun addResponse(response: String) {
        addMessage(response.trim(), SentType.Bot)
    }

    private fun callAPI(question: String) {

        val message = JSONObject()
        message.put("role", "user")
        message.put("content", question)

        val messages = JSONArray()
        messages.put(message)

        val jsonBody = JSONObject()
        jsonBody.put("model", "gpt-3.5-turbo")
        jsonBody.put("messages", messages)
        jsonBody.put("max_tokens", 4000)
        jsonBody.put("temperature", 0)


        val body = RequestBody.create(json, jsonBody.toString())
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer YOUR_API_KEY")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                addResponse("Failed to connect to server " + e.message )
            }

            @Throws(java.io.IOException::class)
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body!!.string())
                    val jsonArray = jsonObject.getJSONArray("choices")
                    val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                    addResponse(result)
                } else {
                    addResponse("Failed to connect to server " +response.body!!.string())
                }
            }
        })
    }
}
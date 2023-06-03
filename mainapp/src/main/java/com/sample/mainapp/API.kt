package com.sample.mainapp

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class API {
    companion object {
        var API_URL = "https://api.openai.com/v1/chat/completions"
        var API = "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"
        private const val SIXTY_SECOND = 60L
        private const val THIRTY_SECOND = 30L
        val JSON: MediaType = "application/json; charset=utf-8".toMediaType()
    }

    private var client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(SIXTY_SECOND, TimeUnit.SECONDS)
        .callTimeout(THIRTY_SECOND, TimeUnit.SECONDS)
        .readTimeout(THIRTY_SECOND, TimeUnit.SECONDS)
        .writeTimeout(THIRTY_SECOND, TimeUnit.SECONDS)
        .build()

    suspend  fun  callAPI(question: String?): Flow<String> {
        val jsonBody = JSONObject()
        val jsonQBody = JSONObject()
        try {
            jsonQBody.put("role", "user")
            jsonQBody.put("content", question)
            jsonBody.put("model", "gpt-3.5-turbo")
            jsonBody.put("messages", JSONArray().put(jsonQBody))
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0.1)
            println(jsonBody)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        val requestBody: RequestBody = RequestBody.create(JSON , jsonBody.toString())
        val request: Request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + API)
            .post(requestBody)
            .build()
        return callbackFlow<String> {
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to load response due to " + e.message)
                        trySend("Failed to load response due to " + e.message)
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            var jsonObject: JSONObject? = null
                            try {
                                jsonObject = JSONObject(response.body!!.string())
                                val jsonArray = jsonObject.getJSONArray("choices")
                                val msgContent = jsonArray.getJSONObject(0).getJSONObject("message")
                                val result = msgContent.getString("content")
                                println("isSuccessful = " + result.trim { it <= ' ' })
                                trySend(result.trim { it <= ' ' })
                            } catch (e: JSONException) {
                                throw RuntimeException(e)
                            }
                        } else {
                            trySend("Failed to load response due to " + response.body.toString())
                        }
                    }
                })
            awaitClose{ channel.close()}
        }
    }

    fun setUpThePersona(question: String?): Flow<String> {
        val jsonBody = JSONObject()
        val jsonQBody = JSONObject()
        try {
            jsonQBody.put("role", "user")
            jsonQBody.put("content", question)
            jsonBody.put("model", "gpt-3.5-turbo")
            jsonBody.put("messages", JSONArray().put(jsonQBody))
            jsonBody.put("max_tokens", 4000)
            jsonBody.put("temperature", 0.1)
            println(jsonBody)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }
        val requestBody: RequestBody = RequestBody.create(JSON , jsonBody.toString())
        val request: Request = Request.Builder()
            .url(API_URL)
            .header("Authorization", "Bearer " + API)
            .post(requestBody)
            .build()
        return  callbackFlow {
            client.newCall(request)
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                       trySend( "Failed to load response due to " + e.message)
                    }

                    @Throws(IOException::class)
                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            var jsonObject: JSONObject? = null
                            try {
                                jsonObject = JSONObject(response.body!!.string())
                                val jsonArray = jsonObject.getJSONArray("choices")
                                val msgContent = jsonArray.getJSONObject(0).getJSONObject("message")
                                val result = msgContent.getString("content")
                                trySend(result.trim { it <= ' ' })
                            } catch (e: JSONException) {
                                throw RuntimeException(e)
                            }
                        } else {
                            trySend("Failed to load response due to " + response.body.toString())
                        }
                    }
                })
            awaitClose{ channel.close()}
        }
    }
}

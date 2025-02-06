package Scripts.Functions

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.google.gson.JsonObject

class Gemini {

    fun callGeminiAPI(prompt: String, apiKey: String, callback: (String?) -> kotlin.Unit) {
        val client = OkHttpClient()

        val json = JsonObject().apply {
            addProperty("prompt", prompt)
        }.toString()

        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, json)

        val request = Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$apiKey")
            .post(body)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseData = response.body?.string()
                callback(responseData)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(e.toString())
            }
        }.start()
    }
}
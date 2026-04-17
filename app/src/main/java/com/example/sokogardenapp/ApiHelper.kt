package com.example.sokogardenapp

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header
import org.json.JSONArray
import org.json.JSONObject

class ApiHelper(var context: Context) {
    
    // POST (Generic for Payment or others)
    fun postPayment(api: String, params: RequestParams) {
        val client = AsyncHttpClient(true, 80, 443)
        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONObject?) {
                val message = response?.optString("message") ?: "Request Sent Successfully"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                val message = errorResponse?.optString("message") ?: "Payment Request Failed"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseString: String?, throwable: Throwable?) {
                Toast.makeText(context, "Server Error: $responseString", Toast.LENGTH_LONG).show()
            }
        })
    }

    // POST (Used for Signup)
    fun post(api: String, params: RequestParams, progressBar: ProgressBar? = null, actionButton: Button? = null) {
        progressBar?.visibility = View.VISIBLE
        actionButton?.isEnabled = false
        val client = AsyncHttpClient(true, 80, 443)

        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                
                val message = response?.optString("message")
                if (message != null && message.contains("success", ignoreCase = true)) {
                    Toast.makeText(context, "User Registered", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, message ?: "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                val message = errorResponse?.optString("message") ?: "Operation failed"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    // Login logic
    fun post_login(api: String, params: RequestParams, progressBar: ProgressBar? = null, actionButton: Button? = null) {
        progressBar?.visibility = View.VISIBLE
        actionButton?.isEnabled = false
        val client = AsyncHttpClient(true, 80, 443)

        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                
                val message = response?.optString("message")
                if (message == "Login successful") {
                    val user = response.optJSONObject("user")
                    val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString("username", user?.optString("username") ?: "")
                    editor.putString("email", user?.optString("email") ?: "")
                    editor.apply()

                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, message ?: "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                Toast.makeText(context, errorResponse?.optString("message") ?: "Login failed", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun loadProducts(url: String, recyclerView: RecyclerView, progressBar: ProgressBar? = null) {
        progressBar?.visibility = View.VISIBLE
        val client = AsyncHttpClient(true, 80, 443)

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, response: JSONArray) {
                progressBar?.visibility = View.GONE
                val productList = ProductAdapter.fromJsonArray(response)
                recyclerView.adapter = ProductAdapter(productList)
                recyclerView.layoutManager = GridLayoutManager(context, 2)
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, throwable: Throwable?, errorResponse: JSONObject?) {
                progressBar?.visibility = View.GONE
                Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
            }
        })
    }

    interface CallBack {
        fun onSuccess(result: JSONArray?)
        fun onSuccess(result: JSONObject?)
        fun onFailure(result: String?)
    }
}

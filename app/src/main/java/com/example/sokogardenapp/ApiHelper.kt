package com.example.sokogardenapp

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.RequestParams

import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.StringEntity
import org.json.JSONArray
import org.json.JSONObject

class ApiHelper(var context: Context) {
    //POST (Used for Signup)
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
                    // Show a clean message
                    Toast.makeText(context, "User Registered", Toast.LENGTH_LONG).show()
                    
                    // Redirect directly to MainActivity after successful signup
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
                responseString: String?,
                throwable: Throwable?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                Toast.makeText(context, "Error: $responseString", Toast.LENGTH_LONG).show()
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

    //Requires Access Token (Used for Login)
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
                    val username = user?.optString("username") ?: ""
                    val email = user?.optString("email") ?: ""

                    // 🔐 Save to SharedPreferences
                    val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.putString("username", username)
                    editor.putString("email", email)
                    editor.apply()

                    // Show a clean success message
                    Toast.makeText(context, "Login Successful", Toast.LENGTH_LONG).show()

                    // Redirect to Dashboard (MainActivity)
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, message ?: "Login failed", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseString: String?,
                throwable: Throwable?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                Toast.makeText(context, "Error: $responseString", Toast.LENGTH_LONG).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                progressBar?.visibility = View.GONE
                actionButton?.isEnabled = true
                val message = errorResponse?.optString("message") ?: "Login failed"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

fun loadProducts(url: String, recyclerView: RecyclerView, progressBar: ProgressBar? = null) {
    progressBar?.visibility = View.VISIBLE
    val client = AsyncHttpClient(true, 80, 443)

    client.get(url, object : JsonHttpResponseHandler() {
        override fun onSuccess(
            statusCode: Int,
            headers: Array<out Header>?,
            response: JSONArray
        ) {
            progressBar?.visibility = View.GONE
            val productList = ProductAdapter.fromJsonArray(response)
            val adapter = ProductAdapter(productList)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }

        override fun onFailure(
            statusCode: Int,
            headers: Array<out Header>?,
            responseString: String?,
            throwable: Throwable?
        ) {
            progressBar?.visibility = View.GONE
            Toast.makeText(context, "Failed to load products", Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(
            statusCode: Int,
            headers: Array<out Header>?,
            throwable: Throwable?,
            errorResponse: JSONObject?
        ) {
            progressBar?.visibility = View.GONE
            Toast.makeText(context, "Error occurred while loading products", Toast.LENGTH_SHORT).show()
        }
    })
}

    //GET
    fun get(api: String, callBack: CallBack) {
        val client = AsyncHttpClient(true, 80, 443)
        client.get(context, api, null, "application/json",
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONArray
                ) {
                    callBack.onSuccess(response)
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONObject?
                ) {
                callBack.onSuccess(response)
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    responseString: String?,
                    throwable: Throwable?
                ) {
                    callBack.onFailure(responseString)
                }
            })

    }

    interface CallBack {
        fun onSuccess(result: JSONArray?)
        fun onSuccess(result: JSONObject?)
        fun onFailure(result: String?)
    }
}

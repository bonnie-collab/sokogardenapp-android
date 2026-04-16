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
    //POST
    fun post(api: String, params: RequestParams, progressBar: ProgressBar? = null, actionButton: Button? = null) {
        progressBar?.visibility = View.VISIBLE
        actionButton?.isEnabled = false
        Toast.makeText(context, "Please wait for response", Toast.LENGTH_LONG).show()
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
                    Toast.makeText(context, "Registration Successful. Please Sign In.", Toast.LENGTH_LONG).show()
                    val intent = Intent(context, Signin::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Response: $message", Toast.LENGTH_SHORT).show()
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

    //Requires Access Token
    fun post_login(api: String, params: RequestParams, progressBar: ProgressBar? = null, actionButton: Button? = null) {
        progressBar?.visibility = View.VISIBLE
        actionButton?.isEnabled = false
        Toast.makeText(context, "Please wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)

        client.post(api, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                response: JSONObject?
            ) {
                // IMPORTANT: Always hide progress bar and re-enable button
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

                    Toast.makeText(context, "Welcome $username", Toast.LENGTH_LONG).show()

                    // Redirect to Dashboard
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                } else {
                    // This handles cases where API returns 200 OK but message is "Login failed"
                    Toast.makeText(context, "$message", Toast.LENGTH_LONG).show()
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
            // Changed to GridLayoutManager with 2 columns
            recyclerView.layoutManager = GridLayoutManager(context, 2)
        }

        override fun onFailure(
            statusCode: Int,
            headers: Array<out Header>?,
            responseString: String?,
            throwable: Throwable?
        ) {
            progressBar?.visibility = View.GONE
            Toast.makeText(context, "Failed to load products: $responseString", Toast.LENGTH_SHORT).show()
        }

        override fun onFailure(
            statusCode: Int,
            headers: Array<out Header>?,
            throwable: Throwable?,
            errorResponse: JSONObject?
        ) {
            progressBar?.visibility = View.GONE
            Toast.makeText(context, "Error: ${errorResponse.toString()}", Toast.LENGTH_SHORT).show()
        }
    })
}

    //GET
    fun get(api: String, callBack: CallBack) {
        val client = AsyncHttpClient(true, 80, 443)
        //GET to API
        client.get(context, api, null, "application/json",
            object : JsonHttpResponseHandler() {
             //When a JSOn array is Returned
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONArray
                ) {
                //Push the response to Callback Interface
                    callBack.onSuccess(response)
                }

                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONObject?
                ) {
                //Push the response to Callback Interface
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

    }//END GET


    //PUT
    fun put(api: String, jsonData: JSONObject) {
        Toast.makeText(context, "Please Wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)
        val con_body = StringEntity(jsonData.toString())
        //PUT to API
        client.put(context, api, con_body, "application/json",
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONObject?
                ) {
                    Toast.makeText(context, "Response $response ", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    throwable: Throwable?,
                    errorResponse: JSONObject?
                ) {
                    //Todo handle the error
                    Toast.makeText(
                        context,
                        "Error Occurred" + throwable.toString(),
                        Toast.LENGTH_LONG
                    ).show()

                }
            })
    }//END PUT

    //DELETE
    fun delete(api: String, jsonData: JSONObject) {
        Toast.makeText(context, "Please Wait for response", Toast.LENGTH_LONG).show()
        val client = AsyncHttpClient(true, 80, 443)
        val con_body = StringEntity(jsonData.toString())
        //DELETE to API
        client.delete(context, api, con_body, "application/json",
            object : JsonHttpResponseHandler() {
                override fun onSuccess(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    response: JSONObject?
                ) {
                    Toast.makeText(context, "Response $response ", Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(
                    statusCode: Int,
                    headers: Array<out Header>?,
                    throwable: Throwable?,
                    errorResponse: JSONObject?
                ) {
                    //super.onFailure(statusCode, headers, throwable, errorResponse)
                    //Todo handle the error
                    Toast.makeText(
                        context,
                        "Error Occurred" + throwable.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    // progressbar.visibility = View.GONE


                }
            })
    }//END DELETE

    //Interface to used by the GET function above.
    //All APis responses either JSON array [], JSON Object {}, String ""
    //Are brought here
    interface CallBack {
        fun onSuccess(result: JSONArray?)
        fun onSuccess(result: JSONObject?)
        fun onFailure(result: String?)
    }

}

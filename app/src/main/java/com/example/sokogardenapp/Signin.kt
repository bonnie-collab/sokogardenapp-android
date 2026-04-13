package com.example.sokogardenapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Signin : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signin)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // find the two edit text, a button and a textview by use of their ids
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val btnSignin = findViewById<Button>(R.id.btnSignin)
        val signupText = findViewById<TextView>(R.id.Signuptxt)

        // on the view set on click listener for the textview to navigate you to sign up page
        signupText.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }

        // on the view set on click listener for the button to navigate you to main page we need to interact with API endpoints as we pass the email and password to the API
        btnSignin.setOnClickListener {
            // specify the API endpoint for login
            val api = "https://kbenkamotho.alwaysdata.net/api/signin"


            // create a request params object to pass the email and password to the API
            val data = RequestParams()

            // add the email and password to the request params to hold data of a bundle
            data.put("email", email.text.toString())
            data.put("password", password.text.toString())

            // import the api helper
            val helper = ApiHelper(applicationContext)

            // call the post method to send the data to the API
            helper.post_login(api,data)

        }

    }
}

package com.example.sokogardenapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.loopj.android.http.RequestParams

class Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display (content behind status bar)
        enableEdgeToEdge()

        // Set the layout XML file
        setContentView(R.layout.activity_signup)

        // Adjust padding to avoid overlap with system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //find all views by use of ids
        val username = findViewById<EditText>(R.id.etUsername)
        val email = findViewById<EditText>(R.id.Email)
        val phone = findViewById<EditText>(R.id.etPhone)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnSignup = findViewById<Button>(R.id.btnSignup)
        val signinText = findViewById<TextView>(R.id.tvSigninRedirect)
        val toggle = findViewById<ImageView>(R.id.ivTogglePassword)

        // ===============================
        // Password visibility toggle setup
        // ===============================

        // Set click listener on the eye icon
        toggle.setOnClickListener {

            // Check if password is currently hidden
            if (password.inputType == (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)) {

                // Show password (make text visible)
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD

            } else {

                // Hide password (mask text)
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            // Move cursor to the end after changing input type
            password.setSelection(password.text.length)
        }
        
        //below when a person clicks on the textview it will redirect to the signin page
        signinText.setOnClickListener {
            startActivity(Intent(this, Signin::class.java))
        }

        btnSignup.setOnClickListener {
            
            val uName = username.text.toString().trim()
            val mail = email.text.toString().trim()
            val phoneNum = phone.text.toString().trim()
            val pass = password.text.toString().trim()

            // Validation: Check if any field is empty
            if (uName.isEmpty() || mail.isEmpty() || phoneNum.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                
                // Optional: Set error on specific empty fields
                if (uName.isEmpty()) username.error = "Username required"
                if (mail.isEmpty()) email.error = "Email required"
                if (phoneNum.isEmpty()) phone.error = "Phone number required"
                if (pass.isEmpty()) password.error = "Password required"
                
                return@setOnClickListener
            }

            //specify the API endpoint
            val api = "https://kbenkamotho.alwaysdata.net/api/signup"

            //create a request params
            val data = RequestParams()

            //add the data to the request params/append
            data.put("username", uName)
            data.put("email", mail)
            data.put("phone", phoneNum)
            data.put("password", pass)

            //create an instance of the ApiHelper class
            val helper = ApiHelper(applicationContext)

            //inside the helper class call the post method
            helper.post(api, data)
        }
    }
}

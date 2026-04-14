package com.example.sokogardenapp

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.*
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

        // ===============================
        // Find views
        // ===============================
        val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnSignin = findViewById<Button>(R.id.btnSignin)
        val signupText = findViewById<TextView>(R.id.tvSignupRedirect)
        val togglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        // 👇 NEW: ProgressBar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // ===============================
        // Navigate to Signup
        // ===============================
        signupText.setOnClickListener {
            startActivity(Intent(this, Signup::class.java))
        }

        // ===============================
        // Password toggle
        // ===============================
        var isPasswordVisible = false

        togglePassword.setOnClickListener {
            if (isPasswordVisible) {
                password.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            } else {
                password.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
            isPasswordVisible = !isPasswordVisible
            password.setSelection(password.text.length)
        }

        // ===============================
        // Sign in logic with loading
        // ===============================
        btnSignin.setOnClickListener {
            
            val mail = email.text.toString().trim()
            val pass = password.text.toString().trim()

            // Validation: Check if fields are empty
            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
                
                if (mail.isEmpty()) email.error = "Email required"
                if (pass.isEmpty()) password.error = "Password required"
                
                return@setOnClickListener
            }

            // Show spinner
            progressBar.visibility = View.VISIBLE

            // Disable button to prevent multiple clicks
            btnSignin.isEnabled = false

            val api = "https://kbenkamotho.alwaysdata.net/api/signin"

            val data = RequestParams()
            data.put("email", mail)
            data.put("password", pass)

            val helper = ApiHelper(applicationContext)

            // Call API
            helper.post_login(api, data)
        }
    }
}

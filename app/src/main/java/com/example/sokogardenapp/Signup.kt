package com.example.sokogardenapp

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        // ===============================
        // Password visibility toggle setup
        // ===============================

        // Get reference to password input field
        val password = findViewById<EditText>(R.id.etPassword)

        // Get reference to eye icon (toggle button)
        val toggle = findViewById<ImageView>(R.id.ivTogglePassword)

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
    }
}
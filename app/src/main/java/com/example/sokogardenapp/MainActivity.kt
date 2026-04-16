package com.example.sokogardenapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        button there button by use of ids
        val signupBtn = findViewById<Button>(R.id.signupBtn)

        val signinBtn = findViewById<Button>(R.id.signinBtn)


//        create the intents for the two vals
        signupBtn.setOnClickListener {
            val intent = Intent(this, Signup::class.java)
            startActivity(intent)
        }


//        =======================================================================================
        signinBtn.setOnClickListener {
            val intent = Intent(this, Signin::class.java)
            startActivity(intent)
        }


        // find the recyclerView and the progress bar by use of their IDs

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val progressbar = findViewById<ProgressBar>(R.id.progressBar)



//specify the API URL endpoint for fetching the products (alwaysData)

        val url = "https://bonnie.alwaysdata.net/product/get_products"



// import the helper class

        val helper = ApiHelper(applicationContext)



//inside of the helper class, access the function loadproducts

        helper.loadProducts(url, recyclerView, progressbar)




    }
}
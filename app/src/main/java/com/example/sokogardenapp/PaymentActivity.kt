package com.example.sokogardenapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.loopj.android.http.RequestParams

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payment)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        find the views by use of their ids
        val txtProductName = findViewById<TextView>(R.id.txtProductName)
        val imgProduct = findViewById<ImageView>(R.id.imgProduct)
        val txtProductCost = findViewById<TextView>(R.id.txtProductCost)
        val etPhone = findViewById<EditText>(R.id.phone)
        val btnPay = findViewById<Button>(R.id.pay)

//        retriew data passed to the previous activity
        val product_name = intent.getStringExtra("product_name")
        val product_description = intent.getStringExtra("product_description")
        val product_cost = intent.getIntExtra("product_cost", 0)
        val product_photo = intent.getStringExtra("product_photo")

        // Set the data to the views passed from the previous activity
        txtProductName.text = product_name
        txtProductCost.text = "Ksh $product_cost"
        
        // specify the image url
        val imageUrl = "https://bonnie.alwaysdata.net/static/images/$product_photo"
        
        // Use Glide to load the image
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .into(imgProduct)

        //when the pay button is clicked
        btnPay.setOnClickListener {
            
            // 1. Get and trim the phone number first
            val phoneNumber = etPhone.text.toString().trim()

            // 2. Validate the phone number BEFORE doing anything else
            if (phoneNumber.isEmpty()) {
                etPhone.error = "Phone number is required"
                return@setOnClickListener
            }
            
            // Accepts 10 digits (07...) or 12 digits (254...)
            if (phoneNumber.length != 10 && phoneNumber.length != 12) {
                etPhone.error = "Invalid phone number (Use 10 or 12 digits)"
                return@setOnClickListener
            }

            // specify the API endpoint for paying with M-Pesa
            val api = "https://kbenkamotho.alwaysdata.net/api/mpesa_payment"

            // create a request params
            val params = RequestParams()
            params.put("amount", product_cost)
            params.put("phone", phoneNumber) // Use the validated/trimmed number
          

            // create an API helper
            val helper = ApiHelper(applicationContext)
            
            // 3. Initiate the payment request
            helper.post(api, params)


            // Optional: create an intent to open the M-Pesa app (Sim ToolKit)
            // Note: Most modern M-Pesa integrations use STK Push, so opening the app manually is often not needed.
            val stkIntent = Intent(Intent.ACTION_VIEW)
            stkIntent.setPackage("com.android.stk")

            try {
                startActivity(stkIntent)
            } catch (e: Exception) {
                // STK app might not be accessible directly on all devices
            }
        }
    }
}

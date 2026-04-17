package com.example.sokogardenapp

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

        // Find the views by their ids
        val txtProductName = findViewById<TextView>(R.id.txtProductName)
        val imgProduct = findViewById<ImageView>(R.id.imgProduct)
        val txtProductCost = findViewById<TextView>(R.id.txtProductCost)
        val etPhone = findViewById<EditText>(R.id.phone)
        val btnPay = findViewById<Button>(R.id.pay)

        // Retrieve data passed from the previous activity
        val product_name = intent.getStringExtra("product_name")
        val product_cost = intent.getIntExtra("product_cost", 0)
        val product_photo = intent.getStringExtra("product_photo")

        // Set the data to the views
        txtProductName.text = product_name
        txtProductCost.text = "Ksh $product_cost"
        
        // Image URL (using the correct domain)
        val imageUrl = "https://bonnie.alwaysdata.net/static/images/$product_photo"
        
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.mipmap.ic_launcher)
            .into(imgProduct)

        // Pay button listener
        btnPay.setOnClickListener {
            var phoneNumber = etPhone.text.toString().trim()

            if (phoneNumber.isEmpty()) {
                etPhone.error = "Phone number is required"
                return@setOnClickListener
            }
            
            // Format phone number to 254...
            if (phoneNumber.startsWith("0")) {
                phoneNumber = "254" + phoneNumber.substring(1)
            } else if (phoneNumber.startsWith("+254")) {
                phoneNumber = phoneNumber.substring(1)
            } else if (phoneNumber.length == 9) {
                phoneNumber = "254" + phoneNumber
            }

            if (phoneNumber.length != 12) {
                etPhone.error = "Invalid phone number format. Use 2547XXXXXXXX"
                return@setOnClickListener
            }

            // Using 'bonnie' as the domain for consistency across your app
            val api = "https://bonnie.alwaysdata.net/api/mpesa_payment"

            val params = RequestParams()
            params.put("amount", product_cost)
            params.put("phone", phoneNumber)

            Toast.makeText(this, "Initiating STK Push for Ksh $product_cost...", Toast.LENGTH_LONG).show()

            val helper = ApiHelper(applicationContext)
            // Use the new postPayment method that handles the STK response properly
            helper.postPayment(api, params)
        }
    }
}

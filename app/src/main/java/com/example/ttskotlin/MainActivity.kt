package com.example.ttskotlin

import android.content.Intent
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import android.widget.LinearLayout
import org.json.JSONArray
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import android.view.View
import android.widget.TextView
import android.graphics.Color
import android.util.TypedValue
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var gambarvoice: ImageButton


    private fun setVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Ada yang bisa dibantu?")
        try {
            voiceRecognitionResult.launch(intent)

        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchMedicineProducts(searchTerm: String) {
        // Make API call to search for medicine products
        val apiEndpoint = "https://calm-tan-bee-tux.cyclic.app/alodoc"

        // Create the JSON object to send as the body of the request
        val requestBody = JSONObject().apply {
            put("text", searchTerm)
        }

        val requestQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, apiEndpoint, requestBody,
            { response ->
                // Display the search results in a new container
                val searchResultsContainer = findViewById<LinearLayout>(R.id.searchResultsContainer)
                searchResultsContainer.removeAllViews()

                val jsonObjectResult = response.getJSONObject("result")
                val jsonArray = jsonObjectResult.getJSONArray("products")


                for (i in 0 until jsonArray.length()) {
                    val productObject = jsonArray.getJSONObject(i)

                    // Extract product information
                    val productName = productObject.getString("name")
                    val productThumbnail = productObject.getString("thumbnail_image")
                    val productRating = productObject.getString("rating")
                    val productSold = productObject.getString("total_product_sold")
                    val productPrice = productObject.getJSONObject("prices").getString("display_amount")
                    val productLink = productObject.getString("link")

                    // Create a layout for each product
                    val productLayout = LinearLayout(this)
                    productLayout.orientation = LinearLayout.HORIZONTAL
                    productLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    productLayout.setPadding(20, 20, 20, 20)

                    // Add product image to layout
                    val productImage = ImageView(this)
                    productImage.layoutParams = LinearLayout.LayoutParams(
                        200,
                        200
                    )

                    // Load image using Glide
                    Glide.with(this)
                        .load(productThumbnail)
                        .placeholder(R.drawable.product_placeholder)
                        .error(R.drawable.product_placeholder)
                        .into(productImage)

                    productLayout.addView(productImage)

                    // Add product name and price to layout
                    val productDetailsLayout = LinearLayout(this)
                    productDetailsLayout.orientation = LinearLayout.VERTICAL
                    productDetailsLayout.layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    productDetailsLayout.setPadding(20, 0, 0, 0)

                    val productNameTextView = TextView(this)
                    productNameTextView.text = productName
                    productNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    productNameTextView.setTextColor(Color.BLACK)
                    productDetailsLayout.addView(productNameTextView)

                    val productPriceTextView = TextView(this)
                    productPriceTextView.text = productPrice
                    productPriceTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    productPriceTextView.setTextColor(Color.GRAY)
                    productDetailsLayout.addView(productPriceTextView)

                    val productRatingTextView = TextView(this)
                    productRatingTextView.text = "★ " + productRating + " | " + productSold
                    productRatingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    productRatingTextView.setTextColor(Color.GRAY)
                    productDetailsLayout.addView(productRatingTextView)

                    productLayout.addView(productDetailsLayout)

                    // Add OnClickListener to product layout
                    productLayout.setOnClickListener {
                        val intent = Intent(this, ProductDetailsActivity::class.java)
                        intent.putExtra("productName", productName)
                        intent.putExtra("productThumbnail", productThumbnail)
                        intent.putExtra("productPrice", productPrice)
                        intent.putExtra("productRating", productRating)
                        intent.putExtra("productSold", productSold)
                        intent.putExtra("productLink", productLink)
                        startActivity(intent)
                    }

                    searchResultsContainer.visibility = View.VISIBLE
                    searchResultsContainer.addView(productLayout)
                }

            },

            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                requestQueue.stop() // Stop the request queue after the request is completed
            })
        requestQueue.add(jsonObjectRequest) // Add the request to the request queue
    }


    private val voiceRecognitionResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val resultData = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                editText.setText(resultData?.get(0))

                // Call API to search for medicine products
                searchMedicineProducts(resultData?.get(0).toString())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gambarvoice = findViewById(R.id.gambarvoice)
        editText = findViewById(R.id.editText)

        gambarvoice.setOnClickListener {
            setVoice()
        }
    }
}



package com.example.ttskotlin

import android.content.Intent
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest
import com.android.volley.Request
import android.widget.LinearLayout
import org.json.JSONArray
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
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Coba kamu ucapkan??")
        try {
            voiceRecognitionResult.launch(intent)

        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchMedicineProducts(searchTerm: String) {
        // Make API call to search for medicine products
        val apiEndpoint = "https://magneto.api.halodoc.com/api/v1/buy-medicine/products/search/"
        val requestUrl = apiEndpoint + searchTerm

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = StringRequest(Request.Method.GET, requestUrl,
            { response ->
                // Display the search results in a new container
                val searchResultsContainer = findViewById<LinearLayout>(R.id.searchResultsContainer)
                searchResultsContainer.removeAllViews()

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONArray("result")

                for (i in 0 until jsonArray.length()) {
                    val productObject = jsonArray.getJSONObject(i)

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
                        .load(productObject.getString("image_url"))
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

                    val productName = TextView(this)
                    productName.text = productObject.getString("name")
                    productName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    productName.setTextColor(Color.BLACK)
                    productDetailsLayout.addView(productName)

                    val productPrice = TextView(this)
                    productPrice.text = "Rp ${productObject.getInt("min_price")}"
                    productPrice.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                    productPrice.setTextColor(Color.GRAY)
                    productDetailsLayout.addView(productPrice)

                    productLayout.addView(productDetailsLayout)

                    searchResultsContainer.visibility = View.VISIBLE
                    searchResultsContainer.addView(productLayout)
                }

            },
            { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            })

        requestQueue.add(stringRequest)
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



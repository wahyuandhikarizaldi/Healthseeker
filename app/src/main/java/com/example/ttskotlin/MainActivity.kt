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
import android.widget.Button
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.Gravity
import androidx.appcompat.widget.Toolbar
import com.example.ttskotlin.BookmarkedProduct
import androidx.core.content.ContextCompat
import android.view.MenuItem
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.util.Log


import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var gambarvoice: ImageButton
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var bookmarkedList: ArrayList<BookmarkedProduct>
    private var isButtonClicked = false

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

                    // Add product to layout
                    val productDetailsLayout = LinearLayout(this)
                    productDetailsLayout.orientation = LinearLayout.VERTICAL
                    productDetailsLayout.layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1.0f
                    ) // Set weight to 1

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

                    // Add bookmark button to layout
                    val bookmarkButton = Button(this)
                    bookmarkButton.layoutParams = LinearLayout.LayoutParams(
                        70,
                        70
                    )

                    // Load bookmarks from SharedPreferences and update the bookmark list and status map
                    val bookmarkedList = loadBookmarks()
                    val bookmarkStatusMap = bookmarkedList.associateBy({ it.name }, { true }).toMutableMap()

                    // Check if the product is already bookmarked
                    val isBookmarked = bookmarkStatusMap.getOrElse(productName, { false })
                    if (isBookmarked) {
                        bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_24)
                    } else {
                        bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                    }

                    bookmarkButton.setOnClickListener {

                        // Load bookmarks from SharedPreferences and update the bookmark list and status map
                        val bookmarkedList = loadBookmarks()
                        val bookmarkStatusMap = bookmarkedList.associateBy({ it.name }, { true }).toMutableMap()

                        // Check if the product is already bookmarked
                        val isBookmarked = bookmarkStatusMap.getOrDefault(productName, false)
                        if (isBookmarked) {
                            bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_24)
                        } else {
                            bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                        }

                        // Toggle the bookmarked status
                        val newIsBookmarked = !isBookmarked

                        // Change the bookmark
                        if (newIsBookmarked) {
                            bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_24)
                            val bookmarkedProduct = BookmarkedProduct(productName, productPrice, productRating, productSold, productThumbnail,  productLink)
                            bookmarkedList.add(bookmarkedProduct)
                            bookmarkStatusMap[productName] = true
                            // Save the updated bookmarked items to SharedPreferences
                            saveBookmarks(bookmarkedList)
                        } else {
                            bookmarkButton.setBackgroundResource(R.drawable.baseline_bookmark_border_24)
                            val indexToRemove = bookmarkedList.indexOfFirst { it.name == productName }
                            if (indexToRemove == -1) {
                                println("Product ${productName} not found in list.")
                            } else {
                                try {
                                    val removedProduct = bookmarkedList.removeAt(indexToRemove)
                                    bookmarkStatusMap[removedProduct.name] = false
                                    println("Product ${removedProduct.name} removed from list.")
                                    // Debugging output:
                                    println("List after removal: ${bookmarkedList.joinToString(", ") { it.name }}")
                                    removeBookmark(bookmarkedList)
                                } catch (e: Exception) {
                                    println("Error removing product ${productName}: ${e.message}")
                                }
                            }
                        }



                        // Show a message indicating that the item has been bookmarked or unbookmarked
                        val message = if (newIsBookmarked) "Item bookmarked" else "Item unbookmarked"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }


                    productLayout.addView(bookmarkButton)



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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle the bookmark icon click event here
                startActivity(Intent(this, BookmarksActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun loadBookmarks(): ArrayList<BookmarkedProduct> {
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val bookmarksJson = sharedPreferences.getString("bookmarks", null)
        return if (bookmarksJson != null) {
            Gson().fromJson(bookmarksJson, object : TypeToken<ArrayList<BookmarkedProduct>>() {}.type)
        } else {
            ArrayList()
        }
    }

    private fun removeBookmark(bookmarkedList: MutableList<BookmarkedProduct>) {
        val jsonString = Gson().toJson(bookmarkedList)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        sharedPreferences.edit().putString("bookmarks", jsonString).apply()

    }

    private fun saveBookmarks(bookmarkedList: MutableList<BookmarkedProduct>) {
        val jsonString = Gson().toJson(bookmarkedList)

        // Load the existing bookmarks from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val existingJsonString = sharedPreferences.getString("bookmarks", null)

        // If there are existing bookmarks, update them with any new ones
        if (existingJsonString != null) {
            val existingBookmarks = Gson().fromJson(existingJsonString, Array<BookmarkedProduct>::class.java).toMutableList()
            for (newBookmark in bookmarkedList) {
                val existingBookmarkIndex = existingBookmarks.indexOfFirst { it.name == newBookmark.name }
                if (existingBookmarkIndex >= 0) {
                    val existingBookmark = existingBookmarks[existingBookmarkIndex]
                    val updatedBookmark = BookmarkedProduct(
                        existingBookmark.name,
                        newBookmark.price,
                        newBookmark.rating,
                        newBookmark.sold,
                        newBookmark.image,
                        newBookmark.link
                    )
                    existingBookmarks[existingBookmarkIndex] = updatedBookmark
                } else {
                    existingBookmarks.add(newBookmark)
                }
            }
            val updatedJsonString = Gson().toJson(existingBookmarks)
            sharedPreferences.edit().putString("bookmarks", updatedJsonString).apply()
        } else {
            // Otherwise, just save the new bookmarks to SharedPreferences
            sharedPreferences.edit().putString("bookmarks", jsonString).apply()
        }
    }

    override fun onResume() {
        super.onResume()

        // Call searchMedicineProducts() again to refresh the results when returning to this activity
        val editText = findViewById<EditText>(R.id.editText)
        val userInput: String = editText.text.toString()

        if (isButtonClicked) {
            searchMedicineProducts(userInput)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the toolbar as the action bar for the activity
        val toolbar = findViewById<Toolbar>(R.id.toolbar1)
        toolbar.title = ""
        setSupportActionBar(toolbar)
        val bookmarkIcon = ContextCompat.getDrawable(this, R.drawable.baseline_bookmarks_24)
        if (bookmarkIcon != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(bookmarkIcon)
        }

        // Initialize the SharedPreferences object
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Load the bookmarked items from SharedPreferences
        val bookmarksJson = sharedPreferences.getString("bookmarks", "")
        if (bookmarksJson != null && bookmarksJson.isNotEmpty()) {
            // Deserialize the JSON string into a list of BookmarkedProduct objects
            bookmarkedList = Gson().fromJson(bookmarksJson, object : TypeToken<ArrayList<BookmarkedProduct>>() {}.type)
        } else {
            // If there are no bookmarks, initialize bookmarkedList to an empty list
            bookmarkedList = ArrayList()
        }

        // Clear the bookmarked list
        bookmarkedList.clear()

        // Save the updated bookmarks to SharedPreferences
        saveBookmarks(bookmarkedList)

        gambarvoice = findViewById(R.id.gambarvoice)
        editText = findViewById(R.id.editText)

        gambarvoice.setOnClickListener {
            isButtonClicked = true
            setVoice()
        }
    }
}



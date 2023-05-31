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
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.ViewGroup
import android.os.Parcelable
import android.os.Parcel
import android.view.MenuItem
import com.example.ttskotlin.BookmarkedProduct
import android.widget.ListView
import android.widget.ListAdapter
import android.widget.ArrayAdapter
import androidx.appcompat.widget.Toolbar
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.SharedPreferences
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import java.util.*


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val iv_note = findViewById<LinearLayout>(R.id.imageView)
        iv_note.alpha = 0f
        iv_note.animate().setDuration(1500).alpha(1f).withEndAction {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

    }

}
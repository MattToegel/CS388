package com.ethereallab.purfectcats

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONArray
import org.json.JSONObject

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CatImageAdapter
    private lateinit var textViewNoResults: TextView
    private lateinit var editTextPage: EditText
    private lateinit var editTextLimit: EditText
    private lateinit var spinnerBreeds: Spinner
    private lateinit var spinnerOrder: Spinner
    private val catImages = mutableListOf<String>()
    private val breeds = mutableListOf<String>()
    private val breedIds = mutableListOf<String>() // To store breed IDs for API requests

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        textViewNoResults = findViewById(R.id.textViewNoResults)
        editTextPage = findViewById(R.id.editTextPage)
        editTextLimit = findViewById(R.id.editTextLimit)
        spinnerBreeds = findViewById(R.id.spinnerBreeds)
        spinnerOrder = findViewById(R.id.spinnerOrder)

        // Set up order spinner
        val orderOptions = listOf("asc", "desc", "random")
        spinnerOrder.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, orderOptions)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        adapter = CatImageAdapter(catImages)
        recyclerView.adapter = adapter

        fetchBreeds()

        findViewById<Button>(R.id.buttonSearch).setOnClickListener {
            searchImages()
        }
    }

    private fun fetchBreeds() {
        val client = AsyncHttpClient()
        val requestHeaders = RequestHeaders().apply {
            put("x-api-key", BuildConfig.api_key) // Correct API key usage
        }

        client.get("https://api.thecatapi.com/v1/breeds", requestHeaders, RequestParams(), object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val jsonArray = json.jsonArray
                parseBreeds(jsonArray)
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Log.e("DEBUG", "Error fetching breeds: $response", throwable)
                Toast.makeText(this@SearchActivity, "Failed to fetch breeds", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseBreeds(jsonArray: JSONArray) {
        breeds.clear()
        breedIds.clear()
        breeds.add("Select a breed") // Default option
        breedIds.add("") // Default empty ID for no selection

        for (i in 0 until jsonArray.length()) {
            val breedObject = jsonArray.getJSONObject(i)
            val breedName = breedObject.getString("name")
            val breedId = breedObject.getString("id")
            breeds.add(breedName)
            breedIds.add(breedId)
        }

        spinnerBreeds.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, breeds)
    }

    private fun searchImages() {
        val client = AsyncHttpClient()
        val selectedBreedIndex = spinnerBreeds.selectedItemPosition
        val selectedBreedId = breedIds[selectedBreedIndex]

        val params = RequestParams().apply {
            put("breed_id", selectedBreedId)
            put("page", editTextPage.text.toString().ifEmpty { "0" })
            put("limit", editTextLimit.text.toString().ifEmpty { "10" })
            put("order", spinnerOrder.selectedItem.toString())
        }

        val requestHeaders = RequestHeaders().apply {
            put("x-api-key", BuildConfig.api_key)
        }

        client.get("https://api.thecatapi.com/v1/images/search", requestHeaders, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                val jsonArray = json.jsonArray
                parseCatImages(jsonArray)
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Log.e("DEBUG", "Error fetching images: $response", throwable)
                Toast.makeText(this@SearchActivity, "Failed to fetch images", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun parseCatImages(jsonArray: JSONArray) {
        catImages.clear()
        if (jsonArray.length() == 0) {
            textViewNoResults.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            textViewNoResults.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            for (i in 0 until jsonArray.length()) {
                val imageUrl = jsonArray.getJSONObject(i).getString("url")
                catImages.add(imageUrl)
            }
            adapter.notifyDataSetChanged()
        }
    }
}

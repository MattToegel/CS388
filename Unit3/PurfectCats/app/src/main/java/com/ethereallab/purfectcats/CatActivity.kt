package com.ethereallab.purfectcats

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.RequestHeaders
import com.codepath.asynchttpclient.RequestParams
import com.codepath.asynchttpclient.callback.BinaryHttpResponseHandler
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler
import okhttp3.Headers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import okio.buffer
import okio.source
import java.io.File
import java.io.FileOutputStream

class CatActivity : AppCompatActivity() {

    private lateinit var responseTextView: TextView
    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cat)

        responseTextView = findViewById(R.id.text_view_response)
        imageView = findViewById(R.id.image_view)

        findViewById<Button>(R.id.button_basic_api_test).setOnClickListener { performBasicApiTest() }
        findViewById<Button>(R.id.button_json_api_test).setOnClickListener { performJsonApiTest() }
        findViewById<Button>(R.id.button_upload_image).setOnClickListener { uploadImage() }
        findViewById<Button>(R.id.button_download_image).setOnClickListener { downloadImage() }

        // Set click listener for the new button to open SearchActivity
        findViewById<Button>(R.id.button_open_search_activity).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    private fun performBasicApiTest() {
        val client = AsyncHttpClient()
        client.get("https://api.thecatapi.com/v1/images/search", object : TextHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, response: String) {
                Log.d("DEBUG", response)
                responseTextView.text = "Basic API Response: $response"
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("DEBUG", errorResponse)
                responseTextView.text = "Error: $errorResponse"
            }
        })
    }

    private fun performJsonApiTest() {
        val client = AsyncHttpClient()
        val params = RequestParams().apply {
            put("limit", "5")
            put("page", 0)
        }
        val requestHeaders = RequestHeaders().apply {
            put("x-api-key", BuildConfig.api_key)
        }

        client.get("https://api.thecatapi.com/v1/images/search", requestHeaders, params, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                Log.d("DEBUG", json.jsonArray.toString())
                responseTextView.text = "JSON API Response: ${json.jsonArray.toString()}"
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Log.d("DEBUG", response)
                responseTextView.text = "Error: $response"
            }
        })
    }

    private fun uploadImage() {
        val client = AsyncHttpClient()
        val params = RequestParams()
        val requestHeaders = RequestHeaders().apply {
            put("x-api-key", BuildConfig.api_key) // Ensure API_KEY is correctly referenced
        }

        // Load the image resource as a byte stream
        val bufferedSource = resources.openRawResource(R.raw.cat).source().buffer()
        try {
            // Read the image bytes from the resource
            val source = bufferedSource.readByteString()

            // Set up the RequestBody with the correct media type using the new method
            val body = source.toByteArray().toRequestBody("image/jpeg".toMediaType()) // Use the correct MIME type

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "cat.jpg", body) // Ensure correct file name and type
                .build()

            // Make the POST request with the image data
            client.post(
                "https://api.thecatapi.com/v1/images/upload",
                requestHeaders,
                params,
                requestBody,
                object : JsonHttpResponseHandler() {
                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.d("DEBUG", "Success: ${json.toString()}")
                        responseTextView.text = "Image uploaded successfully: ${json.toString()}"
                    }

                    override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                        Log.e("DEBUG", "Failure: $statusCode - $response", throwable)
                        responseTextView.text = "Upload failed: $response"
                    }
                }
            )
        } catch (e: IOException) {
            Log.e("DEBUG", "Error reading image resource: ${e.message}", e)
            responseTextView.text = "Error reading image resource"
        }
    }


    private fun downloadImage() {
        val client = AsyncHttpClient()
        client.get("https://cdn2.thecatapi.com/images/6eg.jpg", object : BinaryHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, response: Response) {
                try {
                    val mediaStorageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "TEST")
                    if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                        Log.d("DEBUG", "Failed to create directory")
                        responseTextView.text = "Failed to create directory"
                        return
                    }

                    // Save the image file
                    val file = File(mediaStorageDir, "test.jpg")
                    response.body?.byteStream()?.use { data ->
                        FileOutputStream(file).use { outputStream ->
                            data.copyTo(outputStream)
                        }
                    }
                    Log.d("DEBUG", "Image downloaded successfully")
                    responseTextView.text = "Image downloaded successfully"

                    // Display the downloaded image in the ImageView using Glide
                    Glide.with(this@CatActivity).load(file).into(imageView)

                } catch (e: IOException) {
                    Log.e("DEBUG", e.toString())
                    responseTextView.text = "Error downloading image: ${e.message}"
                }
            }

            override fun onFailure(statusCode: Int, headers: Headers?, response: String, throwable: Throwable?) {
                Log.d("DEBUG", response)
                responseTextView.text = "Download failed: $response"
            }
        })
    }
}

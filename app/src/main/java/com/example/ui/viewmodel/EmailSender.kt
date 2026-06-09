package com.example.ui.viewmodel

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

object EmailSender {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    // A live Google Apps Script Web App URL fully pre-configured to dispatch OTPs using standard Google Mail.
    // This allows the applet to send authentic emails directly to the user's Gmail address out-of-the-box!
    private const val DEFAULT_WEB_APP_URL = "https://script.google.com/macros/s/AKfycbog7ueLZuhasbPQWIHV8cE0cl8J0MLbTqLwTwm5lwZ9pk8e17y9edwhsks2hCWMw01Mw/exec"

    /**
     * Sends a real 6-digit verification code to the destination Gmail address.
     * Uses a robust GET request to the deployed Google Apps Script.
     * 
     * @param toEmail The recipient Gmail address
     * @param otp The 6-digit confirmation code
     * @param webAppUrl Optional custom URL deployed by the user
     */
    suspend fun sendOtp(toEmail: String, otp: String, webAppUrl: String = DEFAULT_WEB_APP_URL): Boolean = withContext(Dispatchers.IO) {
        val targetUrl = webAppUrl.ifBlank { DEFAULT_WEB_APP_URL }
        
        // Construct the GET URL with query parameters
        val urlBuilder = java.lang.StringBuilder(targetUrl)
        if (!targetUrl.contains("?")) {
            urlBuilder.append("?")
        } else {
            urlBuilder.append("&")
        }
        urlBuilder.append("email=").append(java.net.URLEncoder.encode(toEmail, "UTF-8"))
        urlBuilder.append("&otp=").append(java.net.URLEncoder.encode(otp, "UTF-8"))
        urlBuilder.append("&app=").append(java.net.URLEncoder.encode("রক্তবন্ধু", "UTF-8"))

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("User-Agent", "Mozilla/5.0 (Android; Mobile)")
            .build()

        try {
            client.newCall(request).execute().use { response: Response ->
                if (response.isSuccessful) {
                    val body = response.body?.string() ?: ""
                    Log.d("EmailSender", "Real OTP sent to $toEmail. Response: $body")
                    // If the Apps Script successfully delivered or scheduled the email
                    return@withContext body.contains("success") || response.code == 200
                } else {
                    Log.e("EmailSender", "HTTP request failed with status: ${response.code}")
                    return@withContext false
                }
            }
        } catch (e: Exception) {
            Log.e("EmailSender", "Network connectivity exception while dispatching OTP: ${e.message}", e)
            return@withContext false
        }
    }
}

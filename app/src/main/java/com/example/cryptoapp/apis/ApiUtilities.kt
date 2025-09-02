package com.example.cryptoapp.apis;

import retrofit2.Retrofit; //Retrofit: The Retrofit class for building the Retrofit instance.
//GsonConverterFactory: The converter factory for parsing JSON responses.
import retrofit2.converter.gson.GsonConverterFactory;

// Define the ApiUtilities class as an object (singleton)
object ApiUtilities {
    // Define a method to get an instance of Retrofit

   /* getInstance Method:
    Retrofit Builder: A new Retrofit instance is built.
    baseUrl: The base URL for the CoinMarketCap API is set.
    addConverterFactory: A GsonConverterFactory is added to handle JSON parsing.
    build: The Retrofit instance is built and returned. */
    fun getInstance(): Retrofit {
        // Build and return a Retrofit instance
        return Retrofit.Builder()
            .baseUrl("https://api.coinmarketcap.com/") // Set the base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Add a converter factory for JSON parsing
            .build() // Build the Retrofit instance
    }
}
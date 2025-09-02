package com.example.cryptoapp.apis;

import com.example.cryptoapp.fragment.models.MarketModel;
import retrofit2.http.GET;
import retrofit2.Response; // Import the Response class from retrofit2

// Define the ApiInterface interface
//GET: The Retrofit annotation for HTTP GET requests.
//Response: The Retrofit class representing the HTTP response.
interface ApiInterface {
    // Define a method to get market data
    @GET("data-api/v3/cryptocurrency/listing?start=1&limit=500") // Retrofit annotation to specify the HTTP GET request and the endpoint
    suspend fun getMarketData(): Response<MarketModel>; // Suspend function to make a network request and return a Response object
} //The suspend keyword indicates that this function can be called from a coroutine.
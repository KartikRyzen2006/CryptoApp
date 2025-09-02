package com.example.cryptoapp.fragment // Package declaration for the fragment

import android.content.Context // Importing Context for shared preferences
import android.os.Bundle // Importing Bundle for passing data
import androidx.fragment.app.Fragment // Importing Fragment for creating a fragment
import android.view.LayoutInflater // Importing LayoutInflater for inflating views
import android.view.View // Importing View for handling UI elements
import android.view.View.GONE // Importing GONE for visibility
import android.view.ViewGroup // Importing ViewGroup for handling view groups
import androidx.lifecycle.lifecycleScope // Importing lifecycleScope for coroutine scope
import com.example.cryptoapp.R // Importing R for resources
import com.example.cryptoapp.adapter.MarketAdapter // Importing MarketAdapter for the recycler view adapter
import com.example.cryptoapp.apis.ApiInterface // Importing ApiInterface for API calls
import com.example.cryptoapp.apis.ApiUtilities // Importing ApiUtilities for API utilities
import com.example.cryptoapp.databinding.FragmentWatchlistBinding // Importing binding for this fragment
import com.example.cryptoapp.fragment.models.CryptoCurrency // Importing CryptoCurrency model
import com.google.gson.Gson // Importing Gson for JSON serialization/deserialization
import com.google.gson.reflect.TypeToken // Importing TypeToken for type conversion
import kotlinx.coroutines.Dispatchers // Importing Dispatchers for coroutine dispatchers
import kotlinx.coroutines.launch // Importing launch for coroutine launch
import kotlinx.coroutines.withContext // Importing withContext for coroutine context switching

// Declaration of the WatchListFragment class which extends Fragment
class WatchListFragment : Fragment() {
    private lateinit var binding: FragmentWatchlistBinding // Late initialization of the binding
    private lateinit var watchlist: ArrayList<String> // List to hold the watchlist symbols
    private lateinit var watchlistItem: ArrayList<CryptoCurrency> // List to hold the watchlist items

    // Override the onCreateView method to set up the fragment's view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWatchlistBinding.inflate(layoutInflater)

        // Read the watchlist data from shared preferences
        readData()

        // Launch a coroutine to fetch market data and update the watchlist
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            if (res.body() != null) {
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    watchlistItem = ArrayList() // Initialize the watchlist items list
                    watchlistItem.clear() // Clear any existing data

                    // Loop through the watchlist symbols and fetch corresponding cryptocurrency data
                    for (watchData in watchlist) {
                        for (item in res.body()!!.data.cryptoCurrencyList) {
                            if (watchData == item.symbol) {
                                watchlistItem.add(item) // Add the item to the watchlist items list
                            }
                        }
                    }

                    // Hide the loading spinner
                    binding.spinKitView.visibility = GONE

                    // Set the adapter for the recycler view with the watchlist items
                    binding.watchlistRecyclerView.adapter = MarketAdapter(requireContext(), watchlistItem, "watchfragment")
                }
            }
        }

        // Return the root view of the binding
        return binding.root
    }

    // Function to read the watchlist data from shared preferences
    private fun readData() {
        val sharedPreferences = requireContext().getSharedPreferences("watchList", Context.MODE_PRIVATE) // Get the shared preferences
        val gson = Gson() // Create a Gson instance
        val json = sharedPreferences.getString("watchList", ArrayList<String>().toString()) // Get the JSON string from shared preferences
        val type = object : TypeToken<ArrayList<String>>() {}.type // Define the type for deserialization
        watchlist = gson.fromJson(json, type) // Deserialize the JSON to an ArrayList
    }
}
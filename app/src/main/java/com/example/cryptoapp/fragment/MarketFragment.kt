package com.example.cryptoapp.fragment // Package declaration for the fragment

import android.os.Bundle // Importing Bundle for passing data
import android.text.Editable // Importing Editable for text editing
import android.text.TextWatcher // Importing TextWatcher for text change events
import androidx.fragment.app.Fragment // Importing Fragment for creating a fragment
import android.view.LayoutInflater // Importing LayoutInflater for inflating views
import android.view.View // Importing View for handling UI elements
import android.view.View.GONE // Importing GONE for visibility
import android.view.ViewGroup // Importing ViewGroup for handling view groups
import androidx.lifecycle.lifecycleScope // Importing lifecycleScope for coroutine scope
import com.example.cryptoapp.adapter.MarketAdapter // Importing MarketAdapter for the recycler view adapter
import com.example.cryptoapp.apis.ApiInterface // Importing ApiInterface for API calls
import com.example.cryptoapp.apis.ApiUtilities // Importing ApiUtilities for API utilities
import com.example.cryptoapp.databinding.FragmentMarketBinding // Importing binding for this fragment
import com.example.cryptoapp.fragment.models.CryptoCurrency // Importing CryptoCurrency model
import kotlinx.coroutines.Dispatchers // Importing Dispatchers for coroutine dispatchers
import kotlinx.coroutines.launch // Importing launch for coroutine launch
import kotlinx.coroutines.withContext // Importing withContext for coroutine context switching
import java.util.Locale // Importing Locale for locale-specific operations

//Search Functionality: The searchCoin function sets up a TextWatcher to
// listen for changes in the search edit text and updates the recycler view based on the search text.
//updateRecyclerView: This function filters the list of cryptocurrencies
// based on the search text and updates the adapter with the filtered list.
//Error Handling: The code assumes that the API call will succeed and
// does not include error handling. You might want to add error handling in a real-world application.

// Declaration of the MarketFragment class which extends Fragment
class MarketFragment : Fragment() {
    private lateinit var binding: FragmentMarketBinding // Late initialization of the binding

    private lateinit var list: List<CryptoCurrency> // List to hold the cryptocurrency data
    private lateinit var adapter: MarketAdapter // Adapter for the recycler view

    // Override the onCreateView method to set up the fragment's view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMarketBinding.inflate(layoutInflater)

        // Initialize the list and adapter
        list = listOf()
        adapter = MarketAdapter(requireContext(), list, "market")

        // Set the adapter for the recycler view
        binding.currencyRecyclerView.adapter = adapter

        // Launch a coroutine to fetch market data
        lifecycleScope.launch(Dispatchers.IO) {
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            if (res.body() != null) {
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    list = res.body()!!.data.cryptoCurrencyList // Update the list with fetched data
                    adapter.updateData(list) // Update the adapter with the new list
                    binding.spinKitView.visibility = GONE // Hide the loading spinner
                }
            }
        }

        // Set up the search functionality
        searchCoin()

        // Return the root view of the binding
        return binding.root
    }

    // Variable to hold the search text
    lateinit var searchText: String

    // Function to set up the search functionality
    private fun searchCoin() {
        // Add a text change listener to the search edit text
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // This method is called when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
                searchText = s.toString().toLowerCase() // Convert the search text to lowercase
                updateRecyclerView() // Update the recycler view with the search results
            }
        })
    }

    // Function to update the recycler view based on the search text
    private fun updateRecyclerView() {
        val data = ArrayList<CryptoCurrency>() // Create an empty list to hold the filtered data

        // Loop through the list of cryptocurrencies
        for (item in list) {
            val coinName = item.name.toLowerCase(Locale.getDefault()) // Convert the coin name to lowercase
            val coinSymbol = item.symbol.lowercase(Locale.getDefault()) // Convert the coin symbol to lowercase

            // Check if the coin name or symbol contains the search text
            if (coinName.contains(searchText) || coinSymbol.contains(searchText)) {
                data.add(item) // Add the item to the filtered list
            }
        }

        // Update the adapter with the filtered list
        adapter.updateData(data)
    }
}
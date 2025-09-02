package com.example.cryptoapp.fragment // Package declaration for the fragment

import android.os.Bundle // Importing Bundle for passing data
import androidx.fragment.app.Fragment // Importing Fragment for creating a fragment
import android.view.LayoutInflater // Importing LayoutInflater for inflating views
import android.view.View // Importing View for handling UI elements
import android.view.ViewGroup // Importing ViewGroup for handling view groups
import androidx.lifecycle.lifecycleScope // Importing lifecycleScope for coroutine scope
import com.example.cryptoapp.adapter.MarketAdapter // Importing MarketAdapter for the recycler view adapter
import com.example.cryptoapp.apis.ApiInterface // Importing ApiInterface for API calls
import com.example.cryptoapp.apis.ApiUtilities // Importing ApiUtilities for API utilities
import com.example.cryptoapp.databinding.FragmentTopGainLoseBinding // Importing binding for this fragment
import com.example.cryptoapp.fragment.models.CryptoCurrency // Importing CryptoCurrency model
import kotlinx.coroutines.Dispatchers // Importing Dispatchers for coroutine dispatchers
import kotlinx.coroutines.launch // Importing launch for coroutine launch
import kotlinx.coroutines.withContext // Importing withContext for coroutine context switching
import java.util.ArrayList // Importing ArrayList for list operations
import java.util.Collections // Importing Collections for sorting

// Declaration of the TopGainLossFragment class which extends Fragment

//getMarketData: This function:
//Retrieves the position argument to determine whether to show top gainers or top losers.
//Launches a coroutine in the IO dispatcher to make the API call.
//Sorts the fetched cryptocurrency data by 24-hour percent change.
//Depending on the position, it selects the top 10 gainers or losers and updates the recycler view adapter on the main thread.
//Sorting: The Collections.sort method sorts the list of cryptocurrencies by their 24-hour percent change in descending order.
//RecyclerView Adapter: The MarketAdapter is used to display the filtered list of cryptocurrencies in the recycler view.
class TopGainLossFragment : Fragment() {
    lateinit var binding: FragmentTopGainLoseBinding // Late initialization of the binding

    // Override the onCreateView method to set up the fragment's view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTopGainLoseBinding.inflate(layoutInflater)

        // Fetch and display market data
        getMarketData()

        // Return the root view of the binding
        return binding.root
    }

    // Function to fetch and display market data
    private fun getMarketData() {
        // Get the position argument passed to the fragment
        val position = requireArguments().getInt("position")

        // Launch a coroutine in the IO dispatcher
        lifecycleScope.launch(Dispatchers.IO) {
            // Make the API call to get market data
            val res = ApiUtilities.getInstance().create(ApiInterface::class.java).getMarketData()

            if (res.body() != null) {
                // Update the UI on the main thread
                withContext(Dispatchers.Main) {
                    // Get the list of cryptocurrencies from the response
                    val dataItem = res.body()!!.data.cryptoCurrencyList

                    // Sort the data by percent change in descending order
                    Collections.sort(dataItem) { o1, o2 ->
                        (o2.quotes[0].percentChange24h.toInt())
                            .compareTo(o1.quotes[0].percentChange24h.toInt())
                    }

                    // Create an ArrayList to hold the top gainers or losers
                    val list = ArrayList<CryptoCurrency>()

                    // Check the position to determine whether to show top gainers or top losers
                    if (position == 0) {
                        // Clear the list and add the top 10 gainers
                        list.clear()
                        for (i in 0..9) {
                            list.add(dataItem[i])
                        }
                        // Set the adapter for the top gainers
                        binding.topGainLoseRecyclerView.adapter = MarketAdapter(
                            requireContext(),
                            list,
                            "home"
                        )
                    } else {
                        // Clear the list and add the top 10 losers
                        list.clear()
                        for (i in 0..9) {
                            list.add(dataItem[dataItem.size - i - 1])
                        }
                        // Set the adapter for the top losers
                        binding.topGainLoseRecyclerView.adapter = MarketAdapter(
                            requireContext(),
                            list,
                            "home"
                        )
                    }
                }
            }
        }
    }
}
package com.example.cryptoapp.fragment // Package declaration for the fragment

import android.os.Bundle // Importing Bundle for passing data
import android.util.Log // Importing Log for logging messages
import androidx.fragment.app.Fragment // Importing Fragment for creating a fragment
import android.view.LayoutInflater // Importing LayoutInflater for inflating views
import android.view.View // Importing View for handling UI elements
import android.view.View.GONE // Importing GONE for visibility
import android.view.View.VISIBLE // Importing VISIBLE for visibility
import android.view.ViewGroup // Importing ViewGroup for handling view groups
import androidx.lifecycle.lifecycleScope // Importing lifecycleScope for coroutine scope
import androidx.viewpager2.widget.ViewPager2 // Importing ViewPager2 for view pager
import com.example.cryptoapp.adapter.TopGainLossPagerAdapter // Importing TopGainLossPagerAdapter for the view pager adapter
import com.example.cryptoapp.adapter.TopMarketAdapter // Importing TopMarketAdapter for the recycler view adapter
import com.example.cryptoapp.apis.ApiInterface // Importing ApiInterface for API calls
import com.example.cryptoapp.apis.ApiUtilities // Importing ApiUtilities for API utilities
import com.example.cryptoapp.databinding.FragmentHomeBinding // Importing binding for this fragment
import com.google.android.material.tabs.TabLayoutMediator // Importing TabLayoutMediator for tab layout
import kotlinx.coroutines.Dispatchers // Importing Dispatchers for coroutine dispatchers
import kotlinx.coroutines.launch // Importing launch for coroutine launch
import kotlinx.coroutines.withContext // Importing withContext for coroutine context switching
//getTopCurrencyList: This method uses a coroutine to fetch market data from the API and updates the UI with the fetched data.
//TabLayoutMediator: This is used to link the ViewPager2 with the TabLayout and set the text for each tab.
//Coroutine Scope: The lifecycleScope ensures that the coroutine is tied to the lifecycle of the fragment.
//Error Handling: Logs are used to handle and log errors during the API call.

// Declaration of the HomeFragment class which extends Fragment
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding // Late initialization of the binding

    // Override the onCreateView method to set up the fragment's view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        // Fetch the top currency list
        getTopCurrencyList()

        // Set up the tab layout
        setTabLayout()

        // Return the root view of the binding
        return binding.root
    }

    // Function to set up the tab layout
    private fun setTabLayout() {
        // Create an instance of the TopGainLossPagerAdapter
        val adapter = TopGainLossPagerAdapter(this)
        binding.contentViewPager.adapter = adapter // Set the adapter for the view pager

        // Register a page change callback for the view pager
        binding.contentViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Change the visibility of the indicators based on the selected tab
                if (position == 0) {
                    binding.topGainIndicator.visibility = VISIBLE // Show the top gain indicator
                    binding.topLoseIndicator.visibility = GONE // Hide the top lose indicator
                } else {
                    binding.topGainIndicator.visibility = GONE // Hide the top gain indicator
                    binding.topLoseIndicator.visibility = VISIBLE // Show the top lose indicator
                }
            }
        })

        // Set up the tab layout mediator
        TabLayoutMediator(binding.tabLayout, binding.contentViewPager) { tab, position ->
            // Set the text for the tabs
            val title = if (position == 0) {
                "Top Gainers" // Text for the first tab
            } else {
                "Top Losers" // Text for the second tab
            }
            tab.text = title // Set the text for the tab
        }.attach() // Attach the mediator to the tab layout and view pager
    }

    // Function to fetch the top currency list from the API
    private fun getTopCurrencyList() {
        // Launch a coroutine in the IO dispatcher
        lifecycleScope.launch(Dispatchers.IO) {
            // Create an instance of the ApiInterface
            val apiInterface = ApiUtilities.getInstance().create(ApiInterface::class.java)

            try {
                // Make the API call to get market data
                val response = apiInterface.getMarketData()
                if (response.isSuccessful) {
                    // If the response is successful, get the market model
                    val marketModel = response.body()
                    Log.d("SHUBH", "getTopCurrencyList: ${marketModel?.data?.cryptoCurrencyList}") // Log the data

                    // Update the UI on the main thread
                    withContext(Dispatchers.Main) {
                        if (marketModel != null) {
                            // Set the adapter for the recycler view with the fetched data
                            binding.topCurrencyRecyclerView.adapter = TopMarketAdapter(requireContext(), marketModel.data.cryptoCurrencyList)
                        }
                    }
                } else {
                    // Log an error message if the response is not successful
                    Log.e("SHUBH", "Failed to fetch data: ${response.code()}")
                }
            } catch (e: Exception) {
                // Log an error message if an exception occurs
                Log.e("SHUBH", "Error fetching data", e)
            }
        }
    }
}
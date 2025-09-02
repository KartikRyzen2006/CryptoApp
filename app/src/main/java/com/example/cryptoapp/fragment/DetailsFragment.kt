package com.example.cryptoapp.fragment // Package declaration for the fragment

import android.content.Context // Importing Context for shared preferences
import android.os.Bundle // Importing Bundle for passing data
import androidx.fragment.app.Fragment // Importing Fragment for creating a fragment
import android.view.LayoutInflater // Importing LayoutInflater for inflating views
import android.view.View // Importing View for handling UI elements
import android.view.ViewGroup // Importing ViewGroup for handling view groups
import androidx.appcompat.widget.AppCompatButton // Importing AppCompatButton for button UI
import androidx.navigation.fragment.navArgs // Importing navArgs for navigation arguments
import com.bumptech.glide.Glide // Importing Glide for image loading
import com.example.cryptoapp.R // Importing R for resources
import com.example.cryptoapp.databinding.FragmentDetailsBinding // Importing binding for this fragment
import com.example.cryptoapp.fragment.models.CryptoCurrency // Importing CryptoCurrency model
import com.google.gson.Gson // Importing Gson for JSON serialization/deserialization
import com.google.gson.reflect.TypeToken // Importing TypeToken for type conversion

// Declaration of the DetailsFragment class which extends Fragment
class DetailsFragment : Fragment() {
    lateinit var binding: FragmentDetailsBinding // Late initialization of the binding

    // Using navArgs to get the arguments passed to this fragment
    private val item: DetailsFragmentArgs by navArgs()

    // Override the onCreateView method to set up the fragment's view
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater) // Inflate the binding

        // Extract the data from the arguments
        val data: CryptoCurrency = item.data!!

        // Load the chart for the cryptocurrency
        loadChart(data)

        // Set up the details view with the cryptocurrency data
        setUpDetails(data)

        // Set up the button click listeners
        setButtonOnClick(data)

        // Add the cryptocurrency to the watchlist
        addToWatchList(data)

        // Return the root view of the binding
        return binding.root
    }

    // Variable to hold the watchlist
    var watchlist : ArrayList<String>? = null

    // Variable to check if the item is in the watchlist
    var watchListIsChecked = false

    // Function to add the cryptocurrency to the watchlist
    private fun addToWatchList(data: CryptoCurrency) {
        readData() // Read the existing watchlist data

        // Check if the cryptocurrency is already in the watchlist
        watchListIsChecked = if (watchlist!!.contains(data.symbol)) {
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star) // Set the button image to a filled star
            true
        } else {
            binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline) // Set the button image to an outlined star
            false
        }

        // Set the button click listener
        binding.addWatchlistButton.setOnClickListener {
            watchListIsChecked =
                if (!watchListIsChecked) {
                    // If the item is not in the watchlist, add it
                    if (!watchlist!!.contains(data.symbol)) {
                        watchlist!!.add(data.symbol)
                    }
                    storeData() // Store the updated watchlist
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star) // Update the button image
                    true
                } else {
                    // If the item is in the watchlist, remove it
                    binding.addWatchlistButton.setImageResource(R.drawable.ic_star_outline) // Update the button image
                    watchlist!!.remove(data.symbol)
                    storeData() // Store the updated watchlist
                    false
                }
        }
    }

    // Function to store the watchlist data in shared preferences
    private fun storeData() {
        val sharedPreferences = requireContext().getSharedPreferences("watchList", Context.MODE_PRIVATE) // Get the shared preferences
        val editor = sharedPreferences.edit() // Get the editor for the shared preferences
        val gson = Gson() // Create a Gson instance
        val json = gson.toJson(watchlist) // Convert the watchlist to JSON
        editor.putString("watchList", json) // Store the JSON in the shared preferences
        editor.apply() // Apply the changes
    }

    // Function to read the watchlist data from shared preferences
    private fun readData() {
        val sharedPreferences = requireContext().getSharedPreferences("watchlist", Context.MODE_PRIVATE) // Get the shared preferences
        val gson = Gson() // Create a Gson instance
        val json = sharedPreferences.getString("watchList", ArrayList<String>().toString()) // Get the JSON string from the shared preferences
        val type = object : TypeToken<ArrayList<String>>(){}.type // Define the type for deserialization
        watchlist = gson.fromJson(json, type) // Deserialize the JSON to an ArrayList
    }

    // Function to set up the button click listeners for the chart intervals
    private fun setButtonOnClick(item: CryptoCurrency) {
        // Get the buttons by their IDs
        val oneMonth = binding.button
        val oneWeek = binding.button1
        val oneDay = binding.button2
        val fourHour = binding.button3
        val oneHour = binding.button4
        val fifteenMinute = binding.button5

        // Define a click listener for the buttons
        val clickListener = View.OnClickListener {
            when(it.id) {
                fifteenMinute.id -> loadChartData(it, "15", item, oneDay, oneMonth, oneWeek, fourHour, oneHour) // Load 15-minute chart data
                oneHour.id -> loadChartData(it, "1H", item, oneDay, oneMonth, oneWeek, fourHour, fifteenMinute) // Load 1-hour chart data
                fourHour.id -> loadChartData(it, "4H", item, oneDay, oneMonth, oneWeek, fifteenMinute, oneHour) // Load 4-hour chart data
                oneDay.id -> loadChartData(it, "1D", item, fifteenMinute, oneMonth, oneWeek, fourHour, oneHour) // Load 1-day chart data
                oneWeek.id -> loadChartData(it, "1W", item, oneDay, oneMonth, fifteenMinute, fourHour, oneHour) // Load 1-week chart data
                oneMonth.id -> loadChartData(it, "1M", item, oneDay, fifteenMinute, oneWeek, fourHour, oneHour) // Load 1-month chart data
            }
        }

        // Set the click listener for each button
        fifteenMinute.setOnClickListener(clickListener)
        oneHour.setOnClickListener(clickListener)
        fourHour.setOnClickListener(clickListener)
        oneDay.setOnClickListener(clickListener)
        oneWeek.setOnClickListener(clickListener)
        oneMonth.setOnClickListener(clickListener)
    }

    // Function to load the chart data for the selected interval
    private fun loadChartData(
        it: View?,
        s: String,
        item: CryptoCurrency,
        oneDay: AppCompatButton,
        oneMonth: AppCompatButton,
        oneWeek: AppCompatButton,
        fourHour: AppCompatButton,
        oneHour: AppCompatButton
    ) {
        // Disable the other buttons
        disableButton(oneDay, oneMonth, oneWeek, fourHour, oneHour)

        // Set the background of the clicked button to active
        it!!.setBackgroundResource(R.drawable.active_button)

        // Enable JavaScript for the WebView
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null) // Set the layer type to software

        // Load the chart URL with the selected interval
        binding.detaillChartWebView.loadUrl(
            "https://s.tradingview.com/widgetembed/?symbol=" + item.symbol
                .toString() + "USD&interval=" + s + "&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides=" +
                    "{}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap." +
                    "com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }

    // Function to disable the buttons
    private fun disableButton(oneDay: AppCompatButton, oneMonth: AppCompatButton, oneWeek: AppCompatButton, fourHour: AppCompatButton, oneHour: AppCompatButton) {
        oneDay.background = null // Clear the background of the button
        oneMonth.background = null
        oneWeek.background = null
        fourHour.background = null
        oneHour.background = null
    }

    // Function to load the initial chart data
    private fun loadChart(item: CryptoCurrency) {
        binding.detaillChartWebView.settings.javaScriptEnabled = true // Enable JavaScript for the WebView
        binding.detaillChartWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null) // Set the layer type to software

        // Load the initial chart URL
        binding.detaillChartWebView.loadUrl(
            "https://s.tradingview.com/widgetembed/?symbol=" + item.symbol
                .toString() + "USD&interval=D&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=" +
                    "F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc%2FUTC&studies_overrides=" +
                    "{}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utm_source=coinmarketcap." +
                    "com&utm_medium=widget&utm_campaign=chart&utm_term=BTCUSDT"
        )
    }

    // Function to set up the details view with the cryptocurrency data
    private fun setUpDetails(data: CryptoCurrency) {
        binding.detailSymbolTextView.text = data.symbol // Set the symbol text

        // Load the cryptocurrency image using Glide
        Glide.with(requireContext()).load(
            "https://s2.coinmarketcap.com/static/img/coins/64x64/" + data.id + ".png"
        ).thumbnail(Glide.with(requireContext()).load(R.drawable.spinner))
            .into(binding.detailImageView)

        // Set the price text
        binding.detailPriceTextView.text =
            "${String.format("$%.04f", data.quotes[0].price)} "

        // Check if the 24-hour change is positive or negative and update the UI accordingly
        if (data.quotes!![0].percentChange24h > 0) {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green)) // Set text color to green
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up) // Set the caret up image
            binding.detailChangeTextView.text = "+ ${String.format("%.02f", data.quotes[0].percentChange24h)} %" // Set the change text
        } else {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red)) // Set text color to red
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down) // Set the caret down image

            binding.detailChangeTextView.text = "${String.format("%.02f", data.quotes[0].percentChange24h)} %" // Set the change text
        }
    }
}
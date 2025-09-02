package com.example.cryptoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.CurrencyItemLayoutBinding
import com.example.cryptoapp.fragment.HomeFragmentDirections
import com.example.cryptoapp.fragment.MarketFragmentDirections
import com.example.cryptoapp.fragment.WatchListFragmentDirections
import com.example.cryptoapp.fragment.models.CryptoCurrency

class MarketAdapter // Constructor to initialize the adapter with context, list of cryptocurrencies, and type
    (
    private val context: Context,
    private var list: List<CryptoCurrency>,
    private val type: String
) :
    RecyclerView.Adapter<MarketAdapter.MarketViewHolder>() {
    // Inner class for the ViewHolder (MarketViewHolder is an inner class that binds the view using CurrencyItemLayoutBinding)
    inner class MarketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding: CurrencyItemLayoutBinding = CurrencyItemLayoutBinding.bind(view)
    }

    // Method to create a new ViewHolder (Creates a new MarketViewHolder by inflating the layout.)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        // Inflate the layout for the item and create a new ViewHolder
        return MarketViewHolder(
            LayoutInflater.from(
                context
            ).inflate(R.layout.currency_item_layout, parent, false)
        )
    }

    // Method to update the data in the adapter
    fun updateData(dataItem: List<CryptoCurrency>) {
        // Update the list and notify the adapter that the data has changed
        list = dataItem
        notifyDataSetChanged()
    }

    // Method to bind the data to the ViewHolder
    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        // Get the current item
        val item = list[position]

        // Set the name and symbol of the cryptocurrency
        holder.binding.currencyNameTextView.text = item.name
        holder.binding.currencySymbolTextView.text = item.symbol

        // Construct the image URL for the cryptocurrency
        val imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/" + item.id + ".png"

        // Load the image using Glide
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.spinner) // Placeholder image while loading
            .error(R.drawable.ic_error) // Fallback image if loading fails
            .into(holder.binding.currencyImageView)

        // Format and set the price of the cryptocurrency
        holder.binding.currencyPriceTextView.text = String.format("$%.02f", item.quotes[0].price)

        // Format and set the 24h change percentage
        if (item.quotes[0].percentChange24h > 0) {
            // Set the text color to green if the change is positive
            holder.binding.currencyChangeTextView.setTextColor(context.resources.getColor(R.color.green))
            holder.binding.currencyChangeTextView.text =
                "+ " + String.format("%.02f", item.quotes[0].percentChange24h) + " %"
        } else {
            // Set the text color to red if the change is negative
            holder.binding.currencyChangeTextView.setTextColor(context.resources.getColor(R.color.red))
            holder.binding.currencyChangeTextView.text =
                String.format("%.02f", item.quotes[0].percentChange24h) + " %"
        }

        // Set an OnClickListener for the item view
        holder.itemView.setOnClickListener { v: View? ->
            // Navigate to the details fragment based on the type
            if ("home" == type) {
                findNavController(v!!).navigate(
                    HomeFragmentDirections.actionHomeFragmentToDetailsFragment2(
                        item
                    )
                )
            } else if ("market" == type) {
                findNavController(v!!).navigate(
                    MarketFragmentDirections.actionMarketFragmentToDetailsFragment2(
                        item
                    )
                )
            } else {
                findNavController(v!!).navigate(
                    WatchListFragmentDirections.actionWatchListFragment2ToDetailsFragment2(
                        item
                    )
                )
            }
        }
    }

    // Method to get the item count
    override fun getItemCount(): Int {
        return list.size
    }
}
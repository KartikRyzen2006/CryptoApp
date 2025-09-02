package com.example.cryptoapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cryptoapp.R
import com.example.cryptoapp.databinding.TopCurrencyLayoutBinding
import com.example.cryptoapp.fragment.HomeFragmentDirections
import com.example.cryptoapp.fragment.WatchListFragmentDirections
import com.example.cryptoapp.fragment.models.CryptoCurrency

class TopMarketAdapter(var context: Context, val list: List<CryptoCurrency>) : RecyclerView.Adapter<TopMarketAdapter.TopMarketViewHolder>() {

    // Inner class for the ViewHolder
    inner class TopMarketViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding = TopCurrencyLayoutBinding.bind(view)
    }

    // Method to create a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopMarketViewHolder {
        // Inflate the layout for the item and create a new ViewHolder
        return TopMarketViewHolder(LayoutInflater.from(context).inflate(R.layout.top_currency_layout, parent, false))
    }

    // Method to bind the data to the ViewHolder
    override fun onBindViewHolder(holder: TopMarketViewHolder, position: Int) {
        val item = list[position] // Get the current item
        holder.binding.topCurrencyNameTextView.text = item.name // Set the name of the cryptocurrency

        // Construct the image URL
        val imageUrl = "https://s2.coinmarketcap.com/static/img/coins/64x64/${item.id}.png"

        // Load the image using Glide
        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.spinner) // Placeholder image while loading
            .error(R.drawable.ic_error) // Fallback image if loading fails
            .into(holder.binding.topCurrencyImageView)

        // Set the percentage change text and color
        if (item.quotes!![0].percentChange24h > 0) {
            // Set the text color to green if the change is positive
            holder.binding.topCurrencyChangeTextView.setTextColor(context.resources.getColor(R.color.green))
            holder.binding.topCurrencyChangeTextView.text = "+ ${String.format("%.02f", item.quotes[0].percentChange24h)} %"
        } else {
            // Set the text color to red if the change is negative
            holder.binding.topCurrencyChangeTextView.setTextColor(context.resources.getColor(R.color.red))
            holder.binding.topCurrencyChangeTextView.text = "${String.format("%.02f", item.quotes[0].percentChange24h)} %"
        }

        // Set an OnClickListener for the item view
        holder.itemView.setOnClickListener {
            // Navigate to the details fragment
            findNavController(it).navigate(HomeFragmentDirections.actionHomeFragmentToDetailsFragment2(item))
        }
    }

    // Method to get the item count
    override fun getItemCount(): Int {
        return list.size // Return the size of the list
    }
}
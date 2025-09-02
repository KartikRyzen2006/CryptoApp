package com.example.cryptoapp.adapter;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.cryptoapp.fragment.TopGainLossFragment;

// Define the TopGainLossPagerAdapter class which extends FragmentStateAdapter
class TopGainLossPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    // Override the getItemCount method to specify the number of fragments in the ViewPager
    override fun getItemCount(): Int {
        return 2 // Return 2 as there are two fragments (Top Gainers and Top Losers)
    }

    // Override the createFragment method to create and return a fragment based on the position
    override fun createFragment(position: Int): Fragment {
        // Create an instance of TopGainLossFragment
        val fragment = TopGainLossFragment()
        // Create a new Bundle to pass data to the fragment
        val bundle = Bundle()
        // Put the position into the bundle
        bundle.putInt("position", position)
        // Set the bundle as the fragment's arguments
        fragment.arguments = bundle
        // Return the fragment
        return fragment
    }
}
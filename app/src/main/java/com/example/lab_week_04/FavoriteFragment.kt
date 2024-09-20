package com.example.lab_week_04

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import android.widget.ImageButton

private const val PREF_NAME = "favorite_coffees"
private const val FAVORITES_KEY = "favorites"

class FavoriteFragment : Fragment() {

    private lateinit var favoriteItems: MutableSet<Int>
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve the favorites from SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        favoriteItems = getFavorites(sharedPrefs)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_favorite, container, false)

        // Set up RecyclerView to display the favorite items
        val recyclerView = rootView.findViewById<RecyclerView>(R.id.favorites_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        favoriteAdapter = FavoriteAdapter(favoriteItems.toList(),
            onFavoriteRemoved = { coffeeId ->
                removeFavorite(coffeeId)
            },
            onCoffeeClicked = { coffeeId ->
                navigateToDetail(coffeeId)
            }
        )
        recyclerView.adapter = favoriteAdapter

        return rootView
    }

    private fun getFavorites(sharedPrefs: android.content.SharedPreferences): MutableSet<Int> {
        val favoriteStrings = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return favoriteStrings.map { it.toInt() }.toMutableSet()
    }

    private fun removeFavorite(coffeeId: Int) {
        // Remove the item from SharedPreferences
        val sharedPrefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(sharedPrefs)
        favorites.remove(coffeeId)
        sharedPrefs.edit().putStringSet(FAVORITES_KEY, favorites.map { it.toString() }.toSet()).apply()

        // Update the list and notify the adapter
        favoriteItems.remove(coffeeId)
        favoriteAdapter.updateItems(favoriteItems.toList())

        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToDetail(coffeeId: Int) {
        // Navigate to the detail page using Navigation component
        val bundle = Bundle().apply {
            putInt(DetailFragment.COFFEE_ID, coffeeId)
        }
        Navigation.findNavController(requireView())
            .navigate(R.id.action_favoriteFragment_to_detailFragment, bundle)
    }

    class FavoriteAdapter(
        private var items: List<Int>,
        private val onFavoriteRemoved: (Int) -> Unit,
        private val onCoffeeClicked: (Int) -> Unit
    ) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val favoriteTitle: TextView = view.findViewById(R.id.favorite_title)
            val removeFavoriteButton: ImageButton = view.findViewById(R.id.remove_favorite_button)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.favorite_item_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val itemId = items[position]
            holder.favoriteTitle.text = getCoffeeTitleById(itemId, holder.itemView.context)

            // Set click listener for the coffee title (to navigate to detail page)
            holder.favoriteTitle.setOnClickListener {
                onCoffeeClicked(itemId)
            }

            // Set click listener for the remove favorite button
            holder.removeFavoriteButton.setOnClickListener {
                onFavoriteRemoved(itemId)
            }
        }

        override fun getItemCount(): Int = items.size

        fun updateItems(newItems: List<Int>) {
            this.items = newItems
            notifyDataSetChanged()
        }

        private fun getCoffeeTitleById(id: Int, context: Context): String {
            return when (id) {
                R.id.affogato -> context.getString(R.string.affogato_title)
                R.id.americano -> context.getString(R.string.americano_title)
                R.id.latte -> context.getString(R.string.latte_title)
                R.id.fizzy_black -> context.getString(R.string.fizzy_title)
                R.id.beer_coffee -> context.getString(R.string.beerCoffee_title)
                else -> context.getString(R.string.unknown_coffee)
            }
        }
    }
}
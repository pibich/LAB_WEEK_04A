package com.example.lab_week_04

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation

private const val PREF_NAME = "favorite_coffees"
private const val FAVORITES_KEY = "favorites"

class DetailFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null
    private lateinit var coffeeTitle: TextView
    private lateinit var coffeeDesc: TextView
    private lateinit var favoriteButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        coffeeTitle = view.findViewById(R.id.coffee_title)
        coffeeDesc = view.findViewById(R.id.coffee_desc)
        favoriteButton = view.findViewById(R.id.fav_button)

        val coffeeId = arguments?.getInt(COFFEE_ID) ?: R.id.affogato
        setCoffeeData(coffeeId)

        view.findViewById<View>(R.id.back_button).setOnClickListener {
            Navigation.findNavController(view).navigateUp()
        }

        updateFavoriteButton(coffeeId)

        // Handle adding/removing favorite
        favoriteButton.setOnClickListener {
            toggleFavorite(coffeeId)
        }
    }

    private fun setCoffeeData(id: Int) {
        when (id) {
            R.id.affogato -> {
                coffeeTitle.text = getString(R.string.affogato_title)
                coffeeDesc.text = getString(R.string.affogato_desc)
            }

            R.id.americano -> {
                coffeeTitle.text = getString(R.string.americano_title)
                coffeeDesc.text = getString(R.string.americano_desc)
            }

            R.id.latte -> {
                coffeeTitle.text = getString(R.string.latte_title)
                coffeeDesc.text = getString(R.string.latte_desc)
            }

            R.id.fizzy_black -> {
                coffeeTitle.text = getString(R.string.fizzy_title)
                coffeeDesc.text = getString(R.string.fizzy_desc)
            }

            R.id.beer_coffee -> {
                coffeeTitle.text = getString(R.string.beerCoffee_title)
                coffeeDesc.text = getString(R.string.beerCoffee_desc)
            }
        }
    }

    private fun toggleFavorite(coffeeId: Int) {
        val sharedPrefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(sharedPrefs)

        if (favorites.contains(coffeeId)) {
            favorites.remove(coffeeId)
            Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
            favoriteButton.setImageResource(R.drawable.unfavorite)  // Update to unfavorite icon
        } else {
            favorites.add(coffeeId)
            Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
            favoriteButton.setImageResource(R.drawable.favorite)  // Update to favorite icon
        }

        // Save updated favorites to SharedPreferences
        sharedPrefs.edit().putStringSet(FAVORITES_KEY, favorites.map { it.toString() }.toSet()).apply()
    }

    private fun updateFavoriteButton(coffeeId: Int) {
        val sharedPrefs = requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val favorites = getFavorites(sharedPrefs)

        if (favorites.contains(coffeeId)) {
            favoriteButton.setImageResource(R.drawable.favorite)  // Show as favorited
        } else {
            favoriteButton.setImageResource(R.drawable.unfavorite)  // Show as unfavorited
        }
    }

    private fun getFavorites(sharedPrefs: android.content.SharedPreferences): MutableSet<Int> {
        val favoriteStrings = sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()
        return favoriteStrings.map { it.toInt() }.toMutableSet()
    }

    companion object {
        const val COFFEE_ID = "COFFEE_ID"
    }
}

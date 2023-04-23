package com.udacity.asteroidradar.detail


import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentDetailBinding
import timber.log.Timber

class DetailFragment : Fragment() {
    @SuppressLint("StringFormatInvalid")
    override fun onCreateView(inflater: LayoutInflater , container: ViewGroup? ,
                              savedInstanceState: Bundle?): View? {
        Timber.i("DetailFragment onCreateView() called")

        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this

        val asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid

        binding.asteroid = asteroid


        //added this line of code
        binding.activityMainImageOfTheDay.contentDescription = getString(R.string.asteroid_image_description, asteroid.codename)



        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        return binding.root
    }

    private fun displayAstronomicalUnitExplanationDialog() {
        Timber.i("Displaying astronomical unit explanation dialog")
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomica_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }
}

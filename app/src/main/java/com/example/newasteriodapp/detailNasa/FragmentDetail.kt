package com.example.newasteriodapp.detailNasa


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.newasteriodapp.R
import com.example.newasteriodapp.databinding.FragmentDetailBinding

class FragmentDetail : Fragment() {

    private lateinit var binding: FragmentDetailBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        setupUI()

        return binding.root
    }

    private fun setupUI() {
        val asteroid = FragmentDetailArgs.fromBundle(requireArguments()).selectedAsteroid
        binding.asteroid = asteroid

        binding.helpButton.setOnClickListener {
            displayDialog()
        }
    }

    private fun displayDialog() {
        val builder = AlertDialog.Builder(requireActivity())
            .setMessage(getString(R.string.astronomica_unit_explanation))
            .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }
}




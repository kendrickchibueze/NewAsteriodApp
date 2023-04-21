package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.AsteroidItemBinding
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.network.ApiFilter
import timber.log.Timber
import java.util.*

class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(requireActivity().application)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel



        val onAsteroidClickListener = MainAdapter.OnClickListener { asteroid ->
            Timber.i("clicked on an asteriod: %s", asteroid.codename)
            viewModel.displayAsteroidDetails(asteroid)
        }

        binding.asteroidRecycler.adapter = MainAdapter(onAsteroidClickListener)




        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner) { asteroid ->
            asteroid?.let {
                findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
                viewModel.displayAsteroidDetailsComplete()
            }
        }



        setHasOptionsMenu(true)
        return binding.root
    }




    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Check which menu item was selected
        when (item.itemId) {
            R.id.show_week_menu -> {
                // Get today's date
                val today = Calendar.getInstance()
                // Get date one week from now
                val nextWeek = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 7)
                }
                // Update the date range in the view model to only show the next week's asteroids
                viewModel.updateDateRange(today.time, nextWeek.time)
                // Update the filter to show only the next week's asteroids
                viewModel.updateFilter(ApiFilter.SHOW_WEEK)
            }
            R.id.show_today_menu -> {
                // Update the filter to show only today's asteroids
                viewModel.updateFilter(ApiFilter.SHOW_TODAY)
            }
            else -> {
                // Update the filter to show saved asteroids
                viewModel.updateFilter(ApiFilter.SHOW_SAVED)
            }
        }
        // Return true to indicate that the event has been handled
        return true
    }


}



class MainAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<Asteroid, MainAdapter.MainViewHolder>(DiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.bind(asteroid)
        holder.itemView.setOnClickListener { onClickListener.onClick(asteroid) }
    }

    class MainViewHolder private constructor(private val binding: AsteroidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(asteroid: Asteroid) {
            binding.asteroid = asteroid
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MainViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AsteroidItemBinding.inflate(layoutInflater, parent, false)
                return MainViewHolder(binding)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem == newItem
        }
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}

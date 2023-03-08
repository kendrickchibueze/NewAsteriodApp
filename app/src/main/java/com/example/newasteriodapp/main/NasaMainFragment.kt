package com.example.newasteriodapp.main


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.newasteriodapp.R
import com.example.newasteriodapp.databinding.AsteroidItemBinding
import com.example.newasteriodapp.databinding.FragmentMainBinding
import com.example.newasteriodapp.domain.Asteroid
import com.example.newasteriodapp.network.AsteroidApiFilter
import timber.log.Timber
import java.util.*


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)
        binding.lifecycleOwner = this
        viewModel = createViewModel()
        binding.viewModel = viewModel

        binding.asteroidRecycler.adapter = MainAdapter(MainAdapter.OnClickListener {
            Timber.i("you clicked: %s", it.codename)
            viewModel.displayAsteroidDetails(it)
        })



        viewModel.navigateToSelectedProperty.observe(viewLifecycleOwner, Observer { property ->
            property?.let {
                this.findNavController().navigate(
                    MainFragmentDirections.actionShowDetail(it)
                )
                viewModel.displayAsteroidDetailsComplete()
            }
        })





        setHasOptionsMenu(true)
        return binding.root
    }

    private fun createViewModel(): MainViewModel {
        val activity = requireNotNull(this.activity) {

        }
        return ViewModelProvider(this, MainViewModel.Factory(activity.application))
            .get(MainViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_week_menu -> {
                val today = Calendar.getInstance()
                val nextWeek = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 7)
                }
                viewModel.updateDateRange(today.time, nextWeek.time)
                viewModel.updateFilter(AsteroidApiFilter.SHOW_WEEK)
            }
            R.id.show_today_menu -> {
                viewModel.updateFilter(AsteroidApiFilter.SHOW_TODAY)
            }
            else -> {
                viewModel.updateFilter(AsteroidApiFilter.SHOW_SAVED)
            }
        }
        return true
    }
}

class MainAdapter(val onClickListener: OnClickListener) : androidx.recyclerview.widget.ListAdapter<Asteroid , MainAdapter.MainViewHolder>(DiffCallback) {

    class MainViewHolder(val binding: AsteroidItemBinding) :
        RecyclerView.ViewHolder(binding.root) {



        fun bind(item: Asteroid) {
            setAsteroid(item)
            executePendingBindings()
        }

        private fun setAsteroid(item: Asteroid) {
            binding.asteroid = item
        }

        private fun executePendingBindings() {
            binding.executePendingBindings()
        }
    }


    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(AsteroidItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val asteroid = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(asteroid)
        }
        holder.bind(asteroid)
    }

    class OnClickListener(val clickListener: (asteroid: Asteroid) -> Unit) {
        fun onClick(asteroid: Asteroid) = clickListener(asteroid)
    }
}

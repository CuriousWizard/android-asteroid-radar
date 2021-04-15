package com.curiouswizard.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.curiouswizard.asteroidradar.R
import com.curiouswizard.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private var adapter: AsteroidListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_main, container, false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = MainViewModel.MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.viewModel = viewModel

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        viewModel.navigateToDetails.observe(viewLifecycleOwner,{ asteroid ->
            asteroid?.let {
                this.findNavController().navigate(
                        MainFragmentDirections.actionShowDetail(asteroid)
                )
                viewModel.doneNavigating()
            }
        })

        adapter = AsteroidListAdapter(AsteroidListener{
            viewModel.onAsteroidClicked(it)
        })

        binding.asteroidRecycler.adapter = adapter


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.list.observe(viewLifecycleOwner, { list ->
            list?.apply {
                adapter?.asteroids = list
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateList(
            when(item.itemId){
                R.id.show_today_menu -> ListFilter.SHOW_TODAY
                else -> ListFilter.SHOW_WEEK
            }
        )
        return true
    }
}
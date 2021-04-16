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

    // RecyclerView Adapter for converting a list of Asteroids to scrollable list.
    private var adapter: AsteroidListAdapter? = null

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return Return the View for the fragment's UI.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val binding: FragmentMainBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_main, container, false)

        val application = requireNotNull(this.activity).application
        val viewModelFactory = MainViewModel.MainViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.viewModel = viewModel

        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        // Handle if user clicked on an asteroid and navigate to DetailFragment
        viewModel.navigateToDetails.observe(viewLifecycleOwner, { asteroid ->
            asteroid?.let {
                this.findNavController().navigate(
                        MainFragmentDirections.actionShowDetail(asteroid)
                )
                viewModel.doneNavigating()
            }
        })

        adapter = AsteroidListAdapter(AsteroidListener {
            // When an asteroid is clicked this block or lambda will be called by AsteroidListAdapter
            viewModel.onAsteroidClicked(it)
        })

        binding.asteroidRecycler.adapter = adapter

        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, and fragment's
     * view hierarchy has been created.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.list.observe(viewLifecycleOwner, { list ->
            list?.apply {
                adapter?.asteroids = list
            }
        })
    }

    /**
     * Create overflow menu
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Handle changes in overflow menu
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        viewModel.updateList(
                when (item.itemId) {
                    R.id.show_today_menu -> ListFilter.SHOW_TODAY
                    else -> ListFilter.SHOW_WEEK
                }
        )
        return true
    }
}
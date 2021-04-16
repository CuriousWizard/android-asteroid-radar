package com.curiouswizard.asteroidradar.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.curiouswizard.asteroidradar.R
import com.curiouswizard.asteroidradar.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {
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
        // Using DataBinding to inflate fragment view
        val binding: FragmentDetailBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_detail, container, false)

        // Set the lifecycleOwner so DataBinding can observe LiveData
        binding.lifecycleOwner = this

        // Get selected Asteroid from safe-args navigation
        val asteroid = DetailFragmentArgs.fromBundle(requireArguments()).selectedAsteroid

        // Passing selected asteroid to DataBinding
        binding.asteroid = asteroid

        // Show the explanation dialog when user taps on help button
        binding.helpButton.setOnClickListener {
            displayAstronomicalUnitExplanationDialog()
        }

        return binding.root
    }

    /**
     * Create and show an explanation dialog
     */
    private fun displayAstronomicalUnitExplanationDialog() {
        val builder = AlertDialog.Builder(requireActivity())
                .setMessage(getString(R.string.astronomical_unit_explanation))
                .setPositiveButton(android.R.string.ok, null)
        builder.create().show()
    }
}
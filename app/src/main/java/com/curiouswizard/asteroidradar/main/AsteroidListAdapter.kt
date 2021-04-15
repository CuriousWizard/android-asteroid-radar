package com.curiouswizard.asteroidradar.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.curiouswizard.asteroidradar.R
import com.curiouswizard.asteroidradar.databinding.AsteroidListItemBinding
import com.curiouswizard.asteroidradar.model.Asteroid

class AsteroidListAdapter(val clickListener: AsteroidListener) : ListAdapter<Asteroid,AsteroidListAdapter.AsteroidViewHolder>(DiffCallback) {


    companion object DiffCallback : DiffUtil.ItemCallback<Asteroid>() {
        override fun areItemsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Asteroid, newItem: Asteroid): Boolean {
            return oldItem.id == newItem.id
        }
    }

    var asteroids: List<Asteroid> = emptyList()
        set(value) {
            field = value
            // Notify any registered observers that the data set has changed. This will cause every
            // element in our RecyclerView to be invalidated.
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AsteroidViewHolder {
        val withDataBinding: AsteroidListItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                AsteroidViewHolder.LAYOUT,
                parent,
                false)
        return AsteroidViewHolder(withDataBinding)
    }

    override fun onBindViewHolder(holder: AsteroidViewHolder, position: Int) {
        holder.binding.also {
            it.asteroid = asteroids[position]
            it.clickListener = clickListener
        }
    }

    override fun getItemCount() = asteroids.size

    class AsteroidViewHolder(val binding: AsteroidListItemBinding)
        : RecyclerView.ViewHolder(binding.root) {
        companion object {
            @LayoutRes
            val LAYOUT = R.layout.asteroid_list_item
        }
    }


}

class AsteroidListener(val clickListener: (selected: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}
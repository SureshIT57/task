package com.project.gatherly.Model.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.gatherly.Model.Repo.MediaModel
import com.project.gatherly.databinding.GridItemBinding



class GridAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<GridAdapter.MediaViewHolder>() {

    private var mediaList: List<MediaModel> = emptyList()

    fun submitList(list: List<MediaModel>) {
        mediaList = list
        notifyDataSetChanged()
    }

    inner class MediaViewHolder(private val binding: GridItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(media: MediaModel) {
            Glide.with(binding.imageView.context)
                .load(media.url)
                .into(binding.imageView)

            // Set click listener on the root view
            binding.root.setOnClickListener {
                listener.onItemClick(media) // 🔹 Call interface method on click
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val binding = GridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.bind(mediaList[position])
    }

    override fun getItemCount(): Int = mediaList.size
}

interface OnItemClickListener {
    fun onItemClick(media: MediaModel)
}

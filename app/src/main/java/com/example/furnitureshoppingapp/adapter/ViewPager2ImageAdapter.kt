package com.example.furnitureshoppingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.databinding.ViewPagerImageItemBinding

class ViewPager2ImageAdapter: RecyclerView.Adapter<ViewPager2ImageAdapter.ViewPager2ImageViewHolder>() {
    inner class ViewPager2ImageViewHolder(val binding: ViewPagerImageItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(imgPath : String){
            Glide.with(itemView).load(imgPath).into(binding.imageProduct)
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPager2ImageViewHolder {
        return ViewPager2ImageViewHolder( ViewPagerImageItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewPager2ImageViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bindItem(current)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
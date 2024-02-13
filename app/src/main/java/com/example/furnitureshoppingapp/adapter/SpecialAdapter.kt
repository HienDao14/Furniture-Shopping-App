package com.example.furnitureshoppingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.databinding.SpecialRvItemBinding
import com.example.furnitureshoppingapp.model.Product

class SpecialAdapter(val onClick: (Product) -> Unit): RecyclerView.Adapter<SpecialAdapter.SpecialViewHolder>() {

    inner class SpecialViewHolder(val binding: SpecialRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            Glide.with(binding.root).load(product.images[0]).into(binding.ivItem)
            binding.tvCurrentPrice.text = "$ ${product.price.toString()}"
            binding.tvTitle.text = product.name
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialViewHolder {
        return SpecialViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SpecialViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bind(current)
        holder.itemView.setOnClickListener {
            onClick(current)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
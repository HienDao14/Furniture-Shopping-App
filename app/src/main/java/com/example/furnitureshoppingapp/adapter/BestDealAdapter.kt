package com.example.furnitureshoppingapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.databinding.BestDealRvItemBinding
import com.example.furnitureshoppingapp.model.Product

class BestDealAdapter(val onClick: (Product) -> Unit): RecyclerView.Adapter<BestDealAdapter.BestDealViewHolder>() {
    inner class BestDealViewHolder(val binding: BestDealRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(product: Product){
            val newPrice = product.price * (1 - product.offerPercentage!! / 100);
            binding.tvCurrentPrice.text = "$ ${String.format("%.2f", newPrice)}"
            binding.tvTitle.text = product.name
            binding.tvOldPrice.text = "$ ${String.format("%.2f", product.price)}"
            binding.tvOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
            Glide.with(binding.root).load(product.images[0]).into(binding.ivItem)
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealViewHolder {
        return BestDealViewHolder(
            BestDealRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: BestDealViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bindItem(current)
        holder.itemView.setOnClickListener {
            onClick(current)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
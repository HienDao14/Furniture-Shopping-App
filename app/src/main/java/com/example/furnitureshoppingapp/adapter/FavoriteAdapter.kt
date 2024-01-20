package com.example.furnitureshoppingapp.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.databinding.FavoriteRvItemBinding
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product

class FavoriteAdapter: RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {
    inner class FavoriteViewHolder(val binding: FavoriteRvItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bindItem(product: Product){
            binding.tvFavoriteName.text = product.name
            if(product.offerPercentage != null && product.offerPercentage > 0){
                val newPrice = product.price * (1 - product.offerPercentage / 100);
                binding.tvFavoritePrice.text = "$ ${String.format("%.2f", newPrice)}"
                binding.tvFavoriteOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvFavoriteOldPrice.text = "$ ${String.format("%.2f", product.price)}"
            } else {
                binding.tvFavoriteOldPrice.visibility = View.GONE
                binding.tvFavoritePrice.text = "$ ${String.format("%.2f", product.price)}"
            }

            Glide.with(binding.root).load(product.images[0]).into(binding.ivFavoriteItem)
        }

    }

    private val diffCallback = object: DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        return FavoriteViewHolder(FavoriteRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bindItem(current)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(current)
        }

        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((Product) -> Unit)?= null
    var onDeleteClick: ((Product) -> Unit)?= null
}
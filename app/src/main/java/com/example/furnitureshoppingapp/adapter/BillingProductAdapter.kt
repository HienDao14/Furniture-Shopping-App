package com.example.furnitureshoppingapp.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.furnitureshoppingapp.databinding.BillingProductRvItemBinding
import com.example.furnitureshoppingapp.model.CartProduct
import com.example.furnitureshoppingapp.model.Product

class BillingProductAdapter: RecyclerView.Adapter<BillingProductAdapter.BillingProductViewHolder>() {
    inner class BillingProductViewHolder(val binding : BillingProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(cartProduct: CartProduct){
            Glide.with(itemView).load(cartProduct.product.images[0]).into(binding.ivCartItem)
            binding.tvCartName.text = cartProduct.product.name
            var price = cartProduct.product.price
            if(cartProduct.product.offerPercentage != null && cartProduct.product.offerPercentage > 0){
                price = cartProduct.product.price * (1 - cartProduct.product.offerPercentage / 100)
            }
            binding.tvCount.text = "x" + cartProduct.quantity.toString()
            val textPrice = String.format("%.2f", (price * cartProduct.quantity))
            binding.tvCartPrice.text = "$ $textPrice"
            if(cartProduct.selectedColor != null){
                val color = Color.parseColor("#${cartProduct.selectedColor}")
                val imageDrawable = ColorDrawable(color)
                binding.ivColorItem.setImageDrawable(imageDrawable)
            } else {
                binding.ivColorItem.visibility = View.GONE
                binding.tvColor.visibility = View.GONE
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductViewHolder {
        return BillingProductViewHolder(BillingProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: BillingProductViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.bindItem(currentItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
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
import com.example.furnitureshoppingapp.databinding.CartRvItemBinding
import com.example.furnitureshoppingapp.databinding.FragmentCartBinding
import com.example.furnitureshoppingapp.model.CartProduct

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    inner class CartViewHolder(val binding: CartRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(cartProduct: CartProduct, position: Int){
            Glide.with(binding.root).load(cartProduct.product.images[0]).into(binding.ivCartItem)
            binding.tvCartName.text = cartProduct.product.name
            var price = cartProduct.product.price
            if(cartProduct.product.offerPercentage != null && cartProduct.product.offerPercentage > 0){
                price = cartProduct.product.price * (1 - cartProduct.product.offerPercentage / 100)
            }
            binding.tvCount.text = String.format("%02d", cartProduct.quantity)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(CartRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bindItem(current, position)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(current)
        }

        holder.binding.ivAdd.setOnClickListener {
            onIncreaseClick?.invoke(current)
        }
        holder.binding.ivSubtract.setOnClickListener {
            onDecreaseClick?.invoke(current)
        }
        holder.binding.ivDelete.setOnClickListener {
            onDeleteClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onProductClick: ((CartProduct) -> Unit)?= null
    var onIncreaseClick: ((CartProduct) -> Unit)?= null
    var onDecreaseClick: ((CartProduct) -> Unit)?= null
    var onDeleteClick: ((CartProduct) -> Unit)?= null
}
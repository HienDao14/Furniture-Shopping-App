package com.example.furnitureshoppingapp.adapter

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureshoppingapp.R
import com.example.furnitureshoppingapp.databinding.AddressRvItemBinding
import com.example.furnitureshoppingapp.model.Address

class AddressAdapter: RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    var selectedAddress = -1
    inner class AddressViewHolder(val binding: AddressRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(address: Address, isSelected: Boolean){
            binding.buttonAddress.text = address.addressTitle
            if(isSelected){
                binding.buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.primary))
            }
        }
    }

    private val diffCallback = object: DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.address == newItem.address
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder( AddressRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val currentItem = differ.currentList[position]
        holder.bindItem(currentItem, selectedAddress == position)
        holder.binding.buttonAddress.setOnClickListener {
            if(selectedAddress >= 0){
                notifyItemChanged(selectedAddress)
            }
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick: ((Address) -> Unit) ?= null
}
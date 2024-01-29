package com.example.furnitureshoppingapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureshoppingapp.databinding.FragmentShippingAddressBinding
import com.example.furnitureshoppingapp.databinding.ShippingAddressRvItemBinding
import com.example.furnitureshoppingapp.model.Address
import com.google.firebase.auth.oAuthCredential

class ShippingAddressAdapter: RecyclerView.Adapter<ShippingAddressAdapter.ShippingAddressViewHolder>() {

    inner class ShippingAddressViewHolder(val binding: ShippingAddressRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(address: Address){
            
        }
    }

    private val diffCallback =object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.address == newItem.address && oldItem.addressTitle == newItem.addressTitle
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingAddressViewHolder {
        return ShippingAddressViewHolder(ShippingAddressRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ShippingAddressViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
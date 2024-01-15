package com.example.furnitureshoppingapp.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.furnitureshoppingapp.databinding.ColorRvItemBinding

class ColorAdapter: RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {

    private var selectedPosition = -1
    inner class  ColorViewHolder(val binding: ColorRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bindItem(colorCode: String, position: Int){
            val color = Color.parseColor("#${colorCode}")
            val imageDrawable = ColorDrawable(color)
            binding.ivColorItem.setImageDrawable(imageDrawable)
            if(position == selectedPosition){
                binding.ivChosenColor.visibility = View.VISIBLE
            } else {
                binding.ivChosenColor.visibility = View.GONE
            }
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        return ColorViewHolder( ColorRvItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val current = differ.currentList[position]
        holder.bindItem(current, position)
        holder.itemView.setOnClickListener {
            if(selectedPosition >= 0){
                notifyItemChanged(selectedPosition)
            }
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(current)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onItemClick : ((String) -> Unit)?= null
}
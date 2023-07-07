package com.example.conversa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.conversa.databinding.AllChatsItemBinding
import com.example.conversa.databinding.SearchAdapterItemBinding


class SearchAdapter(var onItemClicked: (String) -> Unit) : ListAdapter<String, SearchAdapter.viewHolder>(
    DiffCallback
) {
    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val binding = SearchAdapterItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return viewHolder(binding)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener {
            onItemClicked(getItem(position))
        }
    }
    class viewHolder(private var binding: SearchAdapterItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(username:String){
            binding.username.text = username
        }
    }
}
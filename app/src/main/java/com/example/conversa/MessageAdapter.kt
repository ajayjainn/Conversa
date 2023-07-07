package com.example.conversa

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.conversa.data.Message
import com.example.conversa.databinding.MessageItemReceivedBinding
import com.example.conversa.databinding.MessageItemSentBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(private val currentUsername:String) : ListAdapter<Message,MessageAdapter.CustomViewHolder>(MessageDiffCallback) {

    override fun getItemViewType(position: Int): Int {

        return if(getItem(position).sender==currentUsername){
            Log.d("getitemviewtye 1 $position", getItem(position).sender+currentUsername)
            1
        }else{

            0
        }
    }

    companion object {
        private val MessageDiffCallback=object:DiffUtil.ItemCallback<Message>(){
            override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem ==  newItem
            }

            override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
                return oldItem.time ==newItem.time
            }
        }
    }
    class CustomViewHolder(view: View): RecyclerView.ViewHolder(view){
        val mes = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.textViewMessage)
        val time = view.findViewById<com.google.android.material.textview.MaterialTextView>(R.id.textViewTimestamp)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {

        if(viewType==1){
            val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item_sent,parent,false)
            return CustomViewHolder(v)
        }else{
            val v = LayoutInflater.from(parent.context).inflate(R.layout.message_item_received,parent,false)
            return CustomViewHolder(v)
        }

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        val dateFormat = SimpleDateFormat("h:mm a, d MMMM", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(getItem(position).time))

        holder.time.setText(formattedDate)
        holder.mes.setText(getItem(position).content)

    }

}
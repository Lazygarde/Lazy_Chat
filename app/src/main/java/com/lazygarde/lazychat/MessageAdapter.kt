package com.lazygarde.lazychat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lazygarde.lazychat.databinding.ChatItemBinding

class MessageAdapter(private val messages: List<Message>) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {


    inner class ViewHolder(binding: ChatItemBinding) : RecyclerView.ViewHolder(binding.root) {

        val leftChatView: LinearLayout = binding.leftChatView
        val rightChatView: LinearLayout = binding.rightChatView
        val leftChatText: TextView = binding.leftChatText
        val rightChatText: TextView = binding.rightChatText

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

        )
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        if(message.sendBy == SentType.Me){
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightChatText.text = messages[position].message
        }
        else{
            holder.rightChatView.visibility = View.GONE
            holder.leftChatView.visibility = View.VISIBLE
            holder.leftChatText.text = messages[position].message
        }
    }
}
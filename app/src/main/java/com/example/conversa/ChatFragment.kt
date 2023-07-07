package com.example.conversa

import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conversa.databinding.FragmentChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ChatFragment : Fragment() {
    private val allMessages: MutableLiveData<ArrayList<com.example.conversa.data.Message>> = MutableLiveData(ArrayList())

    private lateinit var binding: FragmentChatBinding
    private var chatid: String? = null
    private var usersUsername:String? = null
    private var receiversUsername:String? = null
    private var database: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            usersUsername = it.getString("users_username")
            receiversUsername = it.getString("receivers_username")
            val smallerUserId = if (receiversUsername!! < usersUsername!!) usersUsername else receiversUsername
            val largerUserId = if (receiversUsername!! >= usersUsername!!) receiversUsername else usersUsername
            chatid = smallerUserId+"_"+largerUserId

            (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(true)
            (requireActivity() as AppCompatActivity).supportActionBar?.title = receiversUsername

            updateUserChats()
        }
    }

    private fun updateUserChats() {
        val ref1 = database.child("UserChats").child(usersUsername!!)
        val ref2 = database.child("UserChats").child(receiversUsername!!)

        ref1.get().addOnSuccessListener { userchat->
            if (!userchat.hasChild(chatid!!)){
                ref1.child(chatid!!).setValue(true)
            }
        }

        ref2.get().addOnSuccessListener { userchat->
            if (!userchat.hasChild(chatid!!)){
                ref2.child(chatid!!).setValue(true)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentChatBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.child("ChatMessages").child(chatid!!).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val messages = arrayListOf<com.example.conversa.data.Message>()
                    for(message in dataSnapshot.children){
                        val m = message.child("content").value as String
                        val t = message.child("time").value as Long
                        val s = message.child("sender").value as String
                        messages.add(com.example.conversa.data.Message(m,s,t))
                    }
                    allMessages.value = messages
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                }
            }
        )


        binding.chatRecyclerView.layoutManager = LinearLayoutManager(requireContext())


        val adapter = MessageAdapter(usersUsername!!)
        binding.chatRecyclerView.adapter = adapter

        allMessages.observe(this.viewLifecycleOwner){
            adapter.submitList(it.toList())
        }

        binding.sendButton.setOnClickListener {

            val message = binding.messageEditText.text.toString()
            val obj = com.example.conversa.data.Message(message,usersUsername!!,System.currentTimeMillis())
            binding.messageEditText.text.clear()

            val ref = database.child("ChatMessages").child(chatid!!).push()
            ref.setValue(obj).addOnSuccessListener {
                Toast.makeText(requireContext(),"sent",Toast.LENGTH_SHORT).show()
            }
        }

        binding.chatRecyclerView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding.chatRecyclerView.smoothScrollToPosition(adapter.itemCount)
        }
    }


}
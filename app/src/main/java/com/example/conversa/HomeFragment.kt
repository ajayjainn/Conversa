package com.example.conversa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conversa.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private var database: DatabaseReference = Firebase.database.reference
    private var allUsers: MutableLiveData<ArrayList<String>> = MutableLiveData(ArrayList())
    private lateinit var currentUsername:String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewChat.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSearchFragment(currentUsername))
        }

        binding.mainProgressBar.visibility=View.VISIBLE

        database.child("Users")
            .child(FirebaseAuth.getInstance().currentUser!!.uid).child("username")
            .get()
            .addOnSuccessListener {
                currentUsername = it.value as String
                fetchChats()
                binding.mainProgressBar.visibility=View.INVISIBLE
            }
        binding.allChatsListView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = AllChatsAdapter{
            createNewChat(it)
        }

        binding.allChatsListView.adapter = adapter

        allUsers.observe(viewLifecycleOwner){
            adapter.submitList(it.toList())
        }

    }

    private fun fetchChats() {
        database.child("UserChats").child(currentUsername).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val allUsername: ArrayList<String> = ArrayList()
                    for(chatId in dataSnapshot.children){
                        val chatid = chatId.key as String
                        if(chatid.split("_")[0]==currentUsername){
                            allUsername.add(chatid.split("_")[1])
                        }else{
                            allUsername.add(chatid.split("_")[0])
                        }
                    }
                    allUsers.value = allUsername
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                }
            }
        )

    }



    private fun createNewChat(recipient: String) {
        if(!this::currentUsername.isInitialized){
            return
        }

        val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(currentUsername,recipient)
        findNavController().navigate(action)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

}
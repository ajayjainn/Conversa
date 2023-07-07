package com.example.conversa

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.conversa.databinding.FragmentSearchBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.Locale

class SearchFragment : Fragment() {
    private lateinit var binding:FragmentSearchBinding
    private lateinit var usersUsername:String
    private var database: DatabaseReference = Firebase.database.reference
    private lateinit var adapter:SearchAdapter
    var allUsernames:ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            usersUsername = it.getString("users_username")!!
        }
        fetchAllUsernames()
    }

    private fun fetchAllUsernames() {
        database.child("TakenUserNames").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val temp: ArrayList<String> = ArrayList()
                    for(uname in dataSnapshot.children){
                        temp.add(uname.key!!)
                    }
                    allUsernames = temp
                    Log.d("Search",allUsernames.joinToString())
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SearchAdapter{
            findNavController().navigate(SearchFragmentDirections.actionSearchFragmentToChatFragment(usersUsername,it))
        }
        binding.filteredUsernames.layoutManager = LinearLayoutManager(requireContext())
        binding.filteredUsernames.adapter = adapter
        binding.usernameInput.addTextChangedListener{
            if(binding.usernameInput.text.toString()==""){
                binding.filteredUsernames.visibility = View.INVISIBLE
            }else{
                binding.filteredUsernames.visibility=View.VISIBLE
            }
            filterUsernames()

        }

    }

    private fun filterUsernames() {
        val text = binding.usernameInput.text.toString()

        val filteredItem = ArrayList<String>()
        // loop through the array list to obtain the required value
        for (uname in allUsernames) {
            if (uname.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))) {
                filteredItem.add(uname)
            }
        }
        Log.d("Search",filteredItem.joinToString())
        adapter.submitList(filteredItem.toList())
    }


}
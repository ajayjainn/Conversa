package com.example.conversa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.conversa.data.User
import com.example.conversa.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class SignUpFragment : Fragment(){
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private var database:DatabaseReference = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        auth = FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
        }
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        binding.btnRegister.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            val username = binding.username.text.toString().replace(" ","").replace("_","")

            if(email.isEmpty() || password.isEmpty() || username.isEmpty()){
                Toast.makeText(requireContext(),"Empty field(s)",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.progressBar.visibility=View.VISIBLE

            database.child("TakenUserNames").child(username).get().addOnSuccessListener {
                if(it.exists()){
                    Toast.makeText(requireContext(),"Username Taken",Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    return@addOnSuccessListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener() { task ->

                        if (task.isSuccessful) {

                            val ref = database.child("Users").child(auth.currentUser!!.uid)
                            ref.setValue(User(username,email))
                            database.child("TakenUserNames").child(username).setValue(true)
                            Toast.makeText(requireContext(),"Account created",Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)

                        } else {
                            Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
                        }
                        binding.progressBar.visibility = View.GONE
                    }
            }

        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }
}
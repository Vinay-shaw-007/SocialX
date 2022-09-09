package com.example.myapplication.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth


class SignUpFragment : Fragment() {


    private lateinit var auth: FirebaseAuth
    private var _binding: FragmentSignUpBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "SignUpFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpBtn.setOnClickListener {
            signUpUser()
        }
    }

    private fun signUpUser() {
        if (validateFields()) {
            binding.progressBar2.visibility = View.VISIBLE
            val email = binding.Email.text.toString()
            val password = binding.Password.text.toString()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    sendUserEmailVerification()
                } else {
                    Log.d(TAG, "Create User Not successful " + it.exception.toString())
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }.addOnFailureListener {
                Log.d(TAG, "Failure create User " + it.message.toString())
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
            }
        } else {
            binding.Email.error = "Field Required"
            binding.Password.error = "Field Required"
            binding.name.error = "Field Required"
            binding.Contact.error = "Field Required"
        }

    }

    private fun sendUserEmailVerification() {

        val user = auth.currentUser
        user?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progressBar2.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Successfully send verification email, Please verify you email. ",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Log.d(TAG, "Verification email Not done successful " + it.exception.toString())
                Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG)
                    .show()
            }
        }?.addOnFailureListener {
            Log.d(TAG, "Failure verification User " + it.message.toString())
            Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
        }

    }

    private fun validateFields(): Boolean {
        val name = binding.name.text.toString()
        val contact = binding.Contact.text.toString()
        val email = binding.Email.text.toString()
        val password = binding.Password.text.toString()
        return !(email.isEmpty() || password.isEmpty() || contact.isEmpty() || name.isEmpty())
    }


}
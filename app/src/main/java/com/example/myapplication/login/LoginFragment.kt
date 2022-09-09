package com.example.myapplication.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentLoginBinding
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInOptions: GoogleSignInClient

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private const val TAG = "LoginFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setFacebookSignIn()
        setGoogleSignInOption()




        binding.googleLoginBtn.setOnClickListener {
            signInGoogle()
        }

        binding.fbLoginBtn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"))
        }

        binding.signInBtn.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            signInWithEmailAndPassword()
        }

        binding.forgotPassword.setOnClickListener {
            if (binding.email.text.toString().isEmpty()){
                binding.email.error = "Email is required to reset the password"
            }else{
                binding.progressBar.visibility = View.VISIBLE
                resetPassword()
            }
        }
    }

    private fun resetPassword() {
        val email = binding.email.text.toString()
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful){
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Check your email to reset your password.", Toast.LENGTH_LONG)
                    .show()
            }else{
                Toast.makeText(requireContext(), "Try again, Something went wrong!", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun signInWithEmailAndPassword() {
        if (validateFields()){
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == true) {
                        binding.progressBar.visibility = View.GONE
                        navigateToMainActivity()
                    } else {
                        Toast.makeText(requireContext(), "Please Verify your email.", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Log.d(TAG, "Failure no success User " + it.exception.toString())
                    Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Log.d(TAG, "Failure signIn User " + it.message.toString())
                Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_LONG).show()
            }
        }else{
            binding.email.error = "Field Required"
            binding.password.error = "Field Required"
        }

    }

    private fun validateFields(): Boolean {
        val email = binding.email.text.toString()
        val password = binding.password.text.toString()
        return !(email.isEmpty() || password.isEmpty())
    }

    private fun setFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    navigateToMainActivity()
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(requireContext(), error.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            })
    }

    private fun setGoogleSignInOption() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInOptions = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun signInGoogle() {
        val signInIntent = googleSignInOptions.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleResult(task)
            }
        }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
            }
        } else Toast.makeText(requireContext(), task.exception.toString(), Toast.LENGTH_LONG).show()
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                navigateToMainActivity()
            } else Toast.makeText(requireContext(), it.exception.toString(), Toast.LENGTH_LONG)
                .show()

        }
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun navigateToMainActivity() {
        binding.email.text = null
        binding.password.text = null
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
    }
}
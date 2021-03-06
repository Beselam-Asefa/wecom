package com.example.wecom.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.wecom.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_sign_up.*
import kotlinx.coroutines.*


class SignupFragment : Fragment(R.layout.fragment_sign_up) {
    lateinit var auth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()

        profile_upfate_bt.setOnClickListener {
            navHostFragment.findNavController()
                .navigate(R.id.action_signUpFragment_to_signInFragment)
        }
        createAccount_bt.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val userName = username_profile_et.text.toString()
        val email = email_profile_et.text.toString()
        val password = profile_page_passw_et.text.toString()
        val confirmPassword = signup_confirm_pw_et.text.toString()

        var validation = validateForm(email, password, confirmPassword, userName)

        if (validation) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                navHostFragment.findNavController()
                                    .navigate(R.id.to_home_screen_inclusive)
                                Toast.makeText(
                                    context, "Default weight is 75 please update your weight",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(
                                    context, "Authentication failed ${task.exception!!.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                //  updateUI(null)
                            }


                        }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun validateForm(
        email: String,
        password: String,
        confirmPassord: String,
        username: String
    ): Boolean {

        when {
            username.isEmpty() -> {
                username_profile_et.error = "username field is empty"
                return false
            }
            username.length > 15 -> {
                username_profile_et.error = "username to long"
                return false
            }
            email.isEmpty() -> {
                email_profile_et.error = "email field is empty"
                return false
            }
            password.isEmpty() -> {
                profile_page_passw_et.error = "password field is empty"
                return false
            }
            password.length < 7 -> {
                profile_page_passw_et.error = "password must be at least 7 characters"
                return false
            }
            confirmPassord.isEmpty() -> {
                signup_confirm_pw_et.error = "confirm password field is empty"
                return false
            }
            password != confirmPassord -> {
                signup_confirm_pw_et.error = "password does not match"
                profile_page_passw_et.error = "password does not match"
            }
        }
        return true

    }

// update profile
    private fun updateProfile(username: String) {
        var user = auth.currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()

        try {
            user?.updateProfile(profileUpdates)

        } catch (e: Exception) {
            Log.d("error", "$e.message")
        }
    }

}
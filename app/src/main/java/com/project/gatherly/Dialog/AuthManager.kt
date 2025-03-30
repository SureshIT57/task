package com.project.gatherly.Dialog

import com.google.firebase.auth.FirebaseAuth

class AuthManager {
    companion object {
        var userId: String? = null
    }

    fun initializeAuth() {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // If no user is signed in, sign in anonymously
            auth.signInAnonymously().addOnSuccessListener {
                userId = auth.currentUser?.uid
                println("Anonymous user signed in: $userId")
            }.addOnFailureListener {
                println("Anonymous sign-in failed: ${it.message}")
            }
        } else {
            // If user is already signed in, store their UID
            userId = currentUser.uid
            println("Existing user: $userId")
        }
    }
}

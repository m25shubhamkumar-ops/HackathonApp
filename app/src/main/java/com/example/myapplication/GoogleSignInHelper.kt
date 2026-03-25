package com.example.myapplication

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class GoogleSignInHelper(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val googleSignInClient: GoogleSignInClient

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /** Returns the intent that launches the Google account picker. */
    fun getSignInIntent(): Intent = googleSignInClient.signInIntent

    /**
     * Processes the result returned from the Google account picker and authenticates
     * the selected account with Firebase.
     */
    fun handleSignInResult(
        data: Intent?,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken != null) {
                firebaseAuthWithGoogle(idToken, onSuccess, onFailure)
            } else {
                onFailure("Google Sign-In failed: ID token is null")
            }
        } catch (e: ApiException) {
            onFailure("Google Sign-In failed (code ${e.statusCode})")
        }
    }

    private fun firebaseAuthWithGoogle(
        idToken: String,
        onSuccess: (FirebaseUser) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        onSuccess(user)
                    } else {
                        onFailure("Authentication succeeded but user is null")
                    }
                } else {
                    onFailure(task.exception?.message ?: "Firebase authentication failed")
                }
            }
    }

    /** Signs out from both Firebase and Google. */
    fun signOut() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
    }

    /** Returns the currently signed-in Firebase user, or null if not signed in. */
    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser
}

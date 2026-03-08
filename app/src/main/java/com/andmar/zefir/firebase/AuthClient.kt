package com.andmar.zefir.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface AuthDefaultClient {

    fun getUid(): String
    fun getUserName(): String

    fun getAuthState(): Flow<Boolean>
    fun signIn(authItem: AuthItem, onActions: (FirebaseActions) -> Unit)
    fun createUser(authItem: AuthItem, onActions: (FirebaseActions) -> Unit)
    fun deleteUser(authItem: AuthItem, onActions: (FirebaseActions) -> Unit)
    fun signOut(onActions: (FirebaseActions) -> Unit)

    fun updateUserName(name: String, onActions: (FirebaseActions) -> Unit)
}

class AuthClient(
    private val auth: FirebaseAuth
): AuthDefaultClient {

    override fun getUid(): String {
        return auth.uid ?: ""
    }

    override fun getUserName(): String {
        return auth.currentUser?.displayName ?: "Noname"
    }

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authState = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }

        auth.addAuthStateListener(authState)

        awaitClose { auth.removeAuthStateListener(authState) }
    }

    override fun signIn(
        authItem: AuthItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        auth.signInWithEmailAndPassword(
            authItem.email,
            authItem.password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onActions(FirebaseActions.Success)
            } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
        }
    }

    override fun createUser(
        authItem: AuthItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        auth.createUserWithEmailAndPassword(
            authItem.email,
            authItem.password
        ).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                val profileUpdates = userProfileChangeRequest {
                    displayName = authItem.name
                }

                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onActions(FirebaseActions.Success)
                        } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
                    }
            } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
        }
    }

    override fun deleteUser(
        authItem: AuthItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun signOut(onActions: (FirebaseActions) -> Unit) {
        onActions(FirebaseActions.Loading)
        try {
            auth.signOut()
        } catch (e: Exception) { onActions(FirebaseActions.Error(e?.message ?: "Error"))}
        onActions(FirebaseActions.Success)
    }

    override fun updateUserName(name: String, onActions: (FirebaseActions) -> Unit) {
        onActions(FirebaseActions.Loading)

        val user = auth.currentUser

        val profileUpdates = userProfileChangeRequest {
            displayName = name
        }

        user?.updateProfile(profileUpdates)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }
}

data class AuthItem(
    val email: String = "",
    val password: String = "",
    val name: String = ""
)
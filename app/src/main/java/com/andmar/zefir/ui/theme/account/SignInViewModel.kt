package com.andmar.zefir.ui.theme.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.andmar.zefir.ui.theme.chat.EditChatNameUiState

class SignInViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient
): ViewModel() {

    var signInUiState by mutableStateOf(SignInUiState())
        private set

    fun onActions(signInActions: SignInActions) {
        when(signInActions) {
            is SignInActions.UpdateSignInUiState -> {
                updateSignInUiState(signInActions.authDetails)
            }
            is SignInActions.SignIn -> { signIn() }
            is SignInActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        signInUiState = SignInUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun updateSignInUiState(authDetails: AuthDetails) {
        signInUiState = SignInUiState(
            authDetails = authDetails,
            isAction = isActions(authDetails)
        )
    }

    private fun signIn() {
        authClient.signIn(
            authItem = signInUiState.authDetails.toAuthItem(),
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun isActions(authDetails: AuthDetails): Boolean {
        return with(authDetails) {
            email.isNotBlank() && password.isNotBlank()
        }
    }
}

data class SignInUiState(
    val authDetails: AuthDetails = AuthDetails(),
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface SignInActions {
    data class UpdateSignInUiState(val authDetails: AuthDetails): SignInActions
    object SignIn: SignInActions
    object Error: SignInActions
}
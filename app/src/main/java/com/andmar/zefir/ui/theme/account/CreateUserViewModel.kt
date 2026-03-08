package com.andmar.zefir.ui.theme.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.andmar.zefir.ui.theme.chat.EditChatNameUiState

class CreateUserViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient
): ViewModel() {

    var createUserUiState by mutableStateOf(CreateUserUiState())
        private set

    fun onActions(createUserActions: CreateUserActions) {
        when(createUserActions) {
            is CreateUserActions.UpdateCreateUserUiState -> {
                updateCreateUserUiState(createUserActions.authDetails)
            }
            is CreateUserActions.CreateUser -> { createUser() }
            is CreateUserActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        createUserUiState = CreateUserUiState(
            firebaseActions = firebaseActions
        )
    }
    private fun updateCreateUserUiState(authDetails: AuthDetails) {
        createUserUiState = CreateUserUiState(
            authDetails = authDetails,
            isAction = isActions(authDetails)
        )
    }

    private fun createUser() {
        authClient.createUser(
            authItem = createUserUiState.authDetails.toAuthItem(),
            onActions = {

                if (it is FirebaseActions.Success) {
                    firestoreClient.setUser(
                        userItem = UserDetails(
                            id = authClient.getUid(),
                            name = createUserUiState.authDetails.name,
                            email = createUserUiState.authDetails.email
                        ).toUserItem(),
                        onActions = { updateFirebaseActions(it) }
                    )
                } else {
                    updateFirebaseActions(it)
                }
            }
        )
    }

    private fun isActions(authDetails: AuthDetails): Boolean {
        return with(authDetails) {
            email.isNotBlank() && password.isNotBlank() && againPassword == password
        }
    }
}

data class CreateUserUiState(
    val authDetails: AuthDetails = AuthDetails(),
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface CreateUserActions {
    data class UpdateCreateUserUiState(val authDetails: AuthDetails): CreateUserActions
    object CreateUser: CreateUserActions
    object Error: CreateUserActions
}
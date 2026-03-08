package com.andmar.zefir.ui.theme.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.AuthItem
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.andmar.zefir.firebase.UserItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AccountViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient
): ViewModel() {

    val userDetailState: StateFlow<UserDetailState> =
        firestoreClient.getUserItem(authClient.getUid()).map { userItem ->
            UserDetailState(userItem.toUserDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UserDetailState()
        )

    var accountUiState by mutableStateOf(AccountUiState())
        private set

    fun onActions(accountActions: AccountActions) {
        when(accountActions) {
            is AccountActions.SignOut -> { signOut() }
            is AccountActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    fun updateName() {
        authClient.updateUserName(
            name = userDetailState.value.userDetails.name,
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        accountUiState = AccountUiState(firebaseActions)
    }

    private fun signOut() {
        authClient.signOut { updateFirebaseActions(it) }
    }
}

data class UserDetailState(val userDetails: UserDetails = UserDetails())

data class AccountUiState(
    val firebaseActions: FirebaseActions = FirebaseActions.None
)

sealed interface AccountActions {
    object SignOut: AccountActions
    object Error: AccountActions
}

data class AuthDetails(
    val email: String = "",
    val name: String= "",
    val password: String = "",
    val againPassword: String = ""
)

data class UserDetails(
    val id: String = "",
    val name: String = "",
    val email: String = ""
)

fun AuthDetails.toAuthItem(): AuthItem = AuthItem(
    email = email,
    password = password,
    name = name
)

fun UserDetails.toUserItem(): UserItem = UserItem(
    id = id,
    name = name,
    email = email
)

fun UserItem.toUserDetails(): UserDetails = UserDetails(
    id = id,
    name = name,
    email = email
)
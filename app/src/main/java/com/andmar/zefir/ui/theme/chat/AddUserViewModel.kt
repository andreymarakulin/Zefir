package com.andmar.zefir.ui.theme.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AddUserViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val chatId: String = savedStateHandle.toRoute<AddUserScreenRoute>().chatId
    var addUserUiState by mutableStateOf(AddUserUiState())
        private set

    fun onActions(addUserActions: AddUserActions) {
        when(addUserActions) {
            is AddUserActions.UpdateAddUserUiState -> {
                updateAddUserUiState(addUserActions.name)
            }
            is AddUserActions.Add -> { setChatItem() }
            is AddUserActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        addUserUiState = AddUserUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun updateAddUserUiState(userId: String) {
        addUserUiState = AddUserUiState(
            userId = userId,
            isAction = isAction(userId)
        )
    }

    private fun setChatItem() {
        viewModelScope.launch {
            val currentChatItem = firestoreClient.getChatItem(chatId)
                .filterNotNull()
                .first()

            val usersId = currentChatItem.usersId.toMutableList()

            usersId.add(addUserUiState.userId)

            firestoreClient.setChat(
                chatItem = currentChatItem.copy(
                    usersId = usersId
                ),
                onActions = { updateFirebaseActions(it) }
            )
        }
    }

    private fun isAction(userId: String): Boolean {
        return userId.isNotBlank()
    }
}

data class AddUserUiState(
    val userId: String = "",
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface AddUserActions {
    data class UpdateAddUserUiState(val name: String): AddUserActions
    object Add: AddUserActions
    object Error: AddUserActions
}
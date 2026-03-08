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

class EditChatNameViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val chatId: String = savedStateHandle.toRoute<AddUserScreenRoute>().chatId
    var editChatNameUiState by mutableStateOf(EditChatNameUiState())
        private set

    init {
        viewModelScope.launch {
            editChatNameUiState = EditChatNameUiState(
                chatDetails = firestoreClient.getChatItem(chatId)
                    .filterNotNull()
                    .first()
                    .toChatDetails()
            )
        }
    }

    fun onActions(editChatNameActions: EditChatNameActions) {
        when(editChatNameActions) {
            is EditChatNameActions.UpdateEditChatNameUiState -> {
                updateEditChatNameUiState(editChatNameActions.chatDetails)
            }
            is EditChatNameActions.Edit -> { editChatName() }
            is EditChatNameActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        editChatNameUiState = EditChatNameUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun updateEditChatNameUiState(chatDetails: ChatDetails) {
        editChatNameUiState = EditChatNameUiState(
            chatDetails = chatDetails,
            isAction = isAction(chatDetails)
        )
    }

    private fun editChatName() {
        firestoreClient.setChat(
            chatItem = editChatNameUiState.chatDetails.toChatItem(),
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun isAction(chatDetails: ChatDetails): Boolean {
        return with(chatDetails) {
            name.isNotEmpty()
        }
    }
}

data class EditChatNameUiState(
    val chatDetails: ChatDetails = ChatDetails(),
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface EditChatNameActions {
    data class UpdateEditChatNameUiState(val chatDetails: ChatDetails): EditChatNameActions
    object Edit: EditChatNameActions
    object Error: EditChatNameActions
}
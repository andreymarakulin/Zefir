package com.andmar.zefir.ui.theme.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatSettingsViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val chatId: String = savedStateHandle.toRoute<AddUserScreenRoute>().chatId
    var chatSettingsUiState by mutableStateOf(ChatSettingsUiState())
        private set

    val chatDetailsState: StateFlow<ChatDetailsState> =
        firestoreClient.getChatItem(chatId).map { chatItem ->
            ChatDetailsState(chatItem.toChatDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ChatDetailsState()
        )

    fun onActions(chatSettingsActions: ChatSettingsActions) {
        when(chatSettingsActions) {
            is ChatSettingsActions.Edit -> {}
            is ChatSettingsActions.DeleteChat -> { deleteChat() }
            is ChatSettingsActions.DeleteUser -> {
                deleteUser(chatSettingsActions.userId)
            }
            is ChatSettingsActions.Out -> { outChat() }
            is ChatSettingsActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        chatSettingsUiState = ChatSettingsUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun setChatItem() {
        firestoreClient.setChat(
            chatItem = chatDetailsState.value.chatDetails.toChatItem(),
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun deleteChat() {
        firestoreClient.deleteChat(
            chatItem = chatDetailsState.value.chatDetails.toChatItem(),
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun deleteUser(userId: String) {
        viewModelScope.launch {
            val currentChatItem = firestoreClient.getChatItem(chatId)
                .filterNotNull()
                .first()

            val usersId = currentChatItem.usersId.toMutableList()

            usersId.remove(userId)

            firestoreClient.setChat(
                chatItem = currentChatItem.copy(
                    usersId = usersId
                ),
                onActions = { updateFirebaseActions(it) }
            )
        }
    }

    private fun outChat() {
        viewModelScope.launch {
            val currentChatItem = firestoreClient.getChatItem(chatId)
                .filterNotNull()
                .first()

            val usersId = currentChatItem.usersId.toMutableList()

            usersId.remove(authClient.getUid())

            firestoreClient.setChat(
                chatItem = currentChatItem.copy(
                    usersId = usersId
                ),
                onActions = { updateFirebaseActions(it) }
            )
        }
    }
}

data class ChatDetailsState(val chatDetails: ChatDetails = ChatDetails())

data class ChatSettingsUiState(
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface ChatSettingsActions {
    object Edit: ChatSettingsActions
    object DeleteChat: ChatSettingsActions
    data class DeleteUser(val userId: String): ChatSettingsActions
    object Out: ChatSettingsActions
    object Error: ChatSettingsActions
}
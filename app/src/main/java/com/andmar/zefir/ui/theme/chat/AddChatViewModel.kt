package com.andmar.zefir.ui.theme.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient

class AddChatViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient
): ViewModel() {

    var addChatUiState by mutableStateOf(AddChatUiState())
        private set

    fun onActions(addChatActions: AddChatActions) {
        when(addChatActions) {
            is AddChatActions.UpdateAddChatDetails -> {
                updateAddChatDetails(addChatActions.addChatDetails)
            }
            is AddChatActions.AddChat -> { addChat() }
            is AddChatActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        addChatUiState = AddChatUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun updateAddChatDetails(addChatDetails: AddChatDetails) {
        addChatUiState = AddChatUiState(
            addChatDetails = addChatDetails,
            isAction = isAction(addChatDetails)
        )
    }

    private fun addChat() {
        firestoreClient.addChat(
            chatItem = ChatDetails(
                name = addChatUiState.addChatDetails.name,
                usersId = listOf(
                    authClient.getUid(),
                    addChatUiState.addChatDetails.uid
                )
            ).toChatItem(),
            onActions = { updateFirebaseActions(it) }
        )
    }

    private fun isAction(addChatDetails: AddChatDetails): Boolean {
        return with(addChatDetails) {
            name.isNotBlank()
        }
    }
}

data class AddChatUiState(
    val addChatDetails: AddChatDetails = AddChatDetails(),
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface AddChatActions {
    data class UpdateAddChatDetails(val addChatDetails: AddChatDetails): AddChatActions
    object AddChat: AddChatActions
    object Error: AddChatActions
}

data class AddChatDetails(
    val name: String = "",
    val uid: String = ""
)
package com.andmar.zefir.ui.theme.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.andmar.zefir.ui.theme.chat.ChatDetails
import com.andmar.zefir.ui.theme.chat.toChatDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient
): ViewModel() {

    val authState: StateFlow<AuthState> =
        authClient.getAuthState().map { authState ->
            AuthState(authState)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = AuthState()
        )

    val chatDetailsListState: StateFlow<ChatDetailsListState> =
        firestoreClient.getChatItems(authClient.getUid()).map { chatItems ->
            ChatDetailsListState(
                chatItems.map { chatItem ->
                    chatItem.toChatDetails()
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ChatDetailsListState(emptyList())
        )

}

data class AuthState(val authState: Boolean = true)
data class ChatDetailsListState(val chatDetailsList: List<ChatDetails>)
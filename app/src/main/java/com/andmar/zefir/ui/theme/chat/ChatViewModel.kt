package com.andmar.zefir.ui.theme.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.ChatItem
import com.andmar.zefir.firebase.FirebaseActions
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.andmar.zefir.firebase.MessageItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.String

class ChatViewModel(
    private val firestoreClient: FirestoreDefaultClient,
    private val authClient: AuthDefaultClient,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    val uid = authClient.getUid()
    val chatId: String = savedStateHandle.toRoute<ChatScreenRoute>().chatId

    val chatDetailsState: StateFlow<ChatDetailsState> =
        firestoreClient.getChatItem(chatId).map { chatItem ->
            ChatDetailsState(chatItem.toChatDetails())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ChatDetailsState()
        )
    val messageDetailsListState: StateFlow<MessageDetailsListState> =
        firestoreClient.getMessageItems(chatId).map { messageItems ->
            MessageDetailsListState(
                messageItems.sortedBy { it.time }.map { messageItem ->
                    messageItem.toMessageDetails(uid)
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = MessageDetailsListState(emptyList())
        )

    var chatUiState by mutableStateOf(ChatUiState())
        private set

    fun onActions(chatActions: ChatActions) {
        when(chatActions) {
            is ChatActions.UpdateMessage -> {
                updateMessageDetails(MessageDetails(message = chatActions.message))
            }
            is ChatActions.Send -> { sendMessage() }
            is ChatActions.Error -> {
                updateFirebaseActions(FirebaseActions.None)
            }
        }
    }

    private fun updateFirebaseActions(firebaseActions: FirebaseActions) {
        chatUiState = ChatUiState(
            firebaseActions = firebaseActions
        )
    }

    private fun updateMessageDetails(messageDetails: MessageDetails) {
        chatUiState = ChatUiState(
            messageDetails = messageDetails,
            isAction = isAction(messageDetails)
        )
    }

    private fun sendMessage() {
        firestoreClient.addMessage(
            messageItem = chatUiState.messageDetails.copy(
                userId = authClient.getUid(),
                userName = authClient.getUserName(),
                chatId = chatId,
                time = System.currentTimeMillis()
            ).toMessageItem(),
            onActions = {

                if (it is FirebaseActions.Success) {
                    chatUiState = ChatUiState()
                    //updateUsersItem()
                } else updateFirebaseActions(it)
            }
        )
    }

    private fun updateUsersItem() {

        viewModelScope.launch {
            val chatItem = firestoreClient.getChatItem(chatId)
                .filterNotNull()
                .first()

            chatItem.usersId.forEach { userId ->

                if (userId != authClient.getUid()) {

                    val userItem = firestoreClient.getUserItem(userId)
                        .filterNotNull()
                        .first()

                    val newMessageList = userItem.newMessageIn.toMutableList()

                    newMessageList.add(chatItem.name)

                    firestoreClient.setUser(
                        userItem = userItem.copy(newMessageIn = newMessageList),
                        onActions = { updateFirebaseActions(it) }
                    )
                }
            }
        }
    }

    private fun isAction(messageDetails: MessageDetails): Boolean {
        return messageDetails.message.isNotBlank()
    }
}
data class MessageDetailsListState(val messageDetailsList: List<MessageDetails>)

data class ChatUiState(
    val messageDetails: MessageDetails = MessageDetails(),
    val firebaseActions: FirebaseActions = FirebaseActions.None,
    val isAction: Boolean = false
)

sealed interface ChatActions {
    data class UpdateMessage(val message: String): ChatActions
    object Send: ChatActions
    object Error: ChatActions
}
data class ChatDetails(
    val id: String = "",
    val name: String = "",
    val usersId: List<String> = emptyList()
)

data class MessageDetails(
    val id: String = "",
    val chatId: String = "",
    val userId: String = "",
    val userName: String = "",
    val message: String = "",
    val time: Long = 0,
    val isMyMessage: Boolean = false
)

fun ChatItem.toChatDetails(): ChatDetails = ChatDetails(
    id = id,
    name = name,
    usersId = usersId
)

fun ChatDetails.toChatItem(): ChatItem = ChatItem(
    id = id,
    name = name,
    usersId = usersId
)

fun MessageItem.toMessageDetails(uid: String): MessageDetails = MessageDetails(
    id = id,
    chatId = chatId,
    userId = userId,
    userName = userName,
    message = message,
    time = time,
    isMyMessage = uid == this.userId
)

fun MessageDetails.toMessageItem(): MessageItem = MessageItem(
    id = id,
    chatId = chatId,
    userId = userId,
    userName = userName,
    message = message,
    time = time
)

fun formater(time: Long): String {
    return ""
}
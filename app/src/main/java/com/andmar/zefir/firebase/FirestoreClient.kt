package com.andmar.zefir.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val USER_COLLECTION = "User"
const val CHAT_COLLECTION = "Chats"
const val MESSAGE_COLLECTIONS = "Message"

interface FirestoreDefaultClient {

    fun setUser(userItem: UserItem, onActions: (FirebaseActions) -> Unit)
    fun getUserItem(uid: String): Flow<UserItem>

    //Chat
    fun addChat(chatItem: ChatItem, onActions: (FirebaseActions) -> Unit)
    fun setChat(chatItem: ChatItem, onActions: (FirebaseActions) -> Unit)
    fun deleteChat(chatItem: ChatItem, onActions: (FirebaseActions) -> Unit)
    fun getChatItem(chatId: String): Flow<ChatItem>
    fun getChatItems(uid: String): Flow<List<ChatItem>>

    // Message
    fun addMessage(messageItem: MessageItem, onActions: (FirebaseActions) -> Unit)
    fun setMessage(messageItem: MessageItem, onActions: (FirebaseActions) -> Unit)
    fun getMessageItems(chatId: String): Flow<List<MessageItem>>
}

class FirestoreClient(
    private val firestore: FirebaseFirestore
): FirestoreDefaultClient {

    override fun setUser(userItem: UserItem, onActions: (FirebaseActions) -> Unit) {
        onActions(FirebaseActions.Loading)

        firestore.collection(USER_COLLECTION).document(userItem.id).set(userItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun getUserItem(uid: String): Flow<UserItem> = callbackFlow {
        val snapshot = firestore.collection(USER_COLLECTION).document(uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    throw exception
                } else {
                    trySend(
                        snapshot?.toObject(UserItem::class.java)
                            ?: UserItem()
                    )
                }
            }
        awaitClose { snapshot.remove() }
    }

    override fun addChat(
        chatItem: ChatItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        firestore.collection(CHAT_COLLECTION).add(chatItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.set(chatItem.copy(id = task.result.id))
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun setChat(
        chatItem: ChatItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        firestore.collection(CHAT_COLLECTION).document(chatItem.id).set(chatItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun deleteChat(
        chatItem: ChatItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        firestore.collection(CHAT_COLLECTION).document(chatItem.id).delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun getChatItem(chatId: String): Flow<ChatItem> = callbackFlow {
        val snapshot = firestore.collection(CHAT_COLLECTION).document(chatId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    throw exception
                } else {
                    trySend(snapshot?.toObject(ChatItem::class.java) ?: ChatItem())
                }
            }
        awaitClose { snapshot.remove() }
    }

    override fun getChatItems(uid: String): Flow<List<ChatItem>> = callbackFlow {
        val snapshot = firestore.collection(CHAT_COLLECTION)
            .whereArrayContains("usersId", uid)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    throw exception
                } else {
                    trySend(snapshot?.toObjects(ChatItem::class.java) ?: emptyList())
                }
            }
        awaitClose { snapshot.remove() }
    }

    override fun addMessage(
        messageItem: MessageItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        firestore.collection(CHAT_COLLECTION).document(messageItem.chatId)
            .collection(MESSAGE_COLLECTIONS).add(messageItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result.set(messageItem.copy(id = task.result.id))
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun setMessage(
        messageItem: MessageItem,
        onActions: (FirebaseActions) -> Unit
    ) {
        onActions(FirebaseActions.Loading)

        firestore.collection(CHAT_COLLECTION).document(messageItem.chatId)
            .collection(MESSAGE_COLLECTIONS).document(messageItem.id).set(messageItem)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onActions(FirebaseActions.Success)
                } else onActions(FirebaseActions.Error(task.exception?.message ?: "Error"))
            }
    }

    override fun getMessageItems(chatId: String): Flow<List<MessageItem>> =callbackFlow {
        val snapshot = firestore.collection(CHAT_COLLECTION).document(chatId)
            .collection(MESSAGE_COLLECTIONS)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    throw exception
                } else {
                    trySend(snapshot?.toObjects(MessageItem::class.java) ?: emptyList())
                }
            }
        awaitClose { snapshot.remove() }
    }
}

sealed interface FirebaseActions {
    object None: FirebaseActions
    object Loading: FirebaseActions
    object Success: FirebaseActions
    data class Error(val message: String): FirebaseActions
}

data class UserItem(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val newMessageIn: List<String> = emptyList()
)

data class ChatItem(
    val id: String = "",
    val name: String = "",
    val usersId: List<String> = emptyList()
)

data class MessageItem(
    val id: String = "",
    val chatId: String = "",
    val userId: String = "",
    val userName: String = "",
    val message: String = "",
    val time: Long = 0
)
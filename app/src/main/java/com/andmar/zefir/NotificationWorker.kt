package com.andmar.zefir

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.andmar.zefir.firebase.AuthClient
import com.andmar.zefir.firebase.FirestoreClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first

class NotificationWorker(val appContext: Context, workerParams: WorkerParameters):
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        checkNewMessage(appContext)

        return Result.success()
    }
}

suspend fun checkNewMessage(context: Context) {

    val firestoreClient = FirestoreClient(Firebase.firestore)
    val authClient = AuthClient(Firebase.auth)
    val notificationClient = NotificationClient(context)


    val userItem = firestoreClient.getUserItem(authClient.getUid())
        .filterNotNull()
        .first()

    userItem.newMessageIn.forEach { chatName ->
        notificationClient.showNewMessageNotification(chatName)
    }
}
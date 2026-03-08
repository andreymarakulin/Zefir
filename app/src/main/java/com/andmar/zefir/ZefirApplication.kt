package com.andmar.zefir

import android.app.Application
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.andmar.zefir.firebase.AuthClient
import com.andmar.zefir.firebase.AuthDefaultClient
import com.andmar.zefir.firebase.FirestoreClient
import com.andmar.zefir.firebase.FirestoreDefaultClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.util.concurrent.TimeUnit

class ZefirApplication: Application() {

    lateinit var firestoreClient: FirestoreDefaultClient
    lateinit var authClient: AuthDefaultClient

    override fun onCreate() {
        super.onCreate()

        firestoreClient = FirestoreClient(Firebase.firestore)
        authClient = AuthClient(Firebase.auth)

        /*
        val notificationWorkRequest: WorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
                .build()

        WorkManager.getInstance(this).enqueue(notificationWorkRequest)

         */
    }
}
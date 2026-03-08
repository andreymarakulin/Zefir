package com.andmar.zefir

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.andmar.zefir.ui.theme.account.AccountViewModel
import com.andmar.zefir.ui.theme.account.CreateUserViewModel
import com.andmar.zefir.ui.theme.account.SignInViewModel
import com.andmar.zefir.ui.theme.chat.AddChatViewModel
import com.andmar.zefir.ui.theme.chat.AddUserViewModel
import com.andmar.zefir.ui.theme.chat.ChatSettingsViewModel
import com.andmar.zefir.ui.theme.chat.ChatViewModel
import com.andmar.zefir.ui.theme.chat.EditChatNameViewModel
import com.andmar.zefir.ui.theme.home.HomeViewModel

object ZefirViewModelProvider {

    val Factory = viewModelFactory {

        initializer {
            HomeViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient
            )
        }

        initializer {
            ChatViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient,
                savedStateHandle = createSavedStateHandle()
            )
        }

        initializer {
            AddChatViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient
            )
        }

        initializer {
            ChatSettingsViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient,
                savedStateHandle = createSavedStateHandle()
            )
        }

        initializer {
            EditChatNameViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                savedStateHandle = createSavedStateHandle()
            )
        }

        initializer {
            AddUserViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                savedStateHandle = createSavedStateHandle()
            )
        }

        initializer {
            AccountViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient
            )
        }

        initializer {
            SignInViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient
            )
        }

        initializer {
            CreateUserViewModel(
                firestoreClient = zefirApplication().firestoreClient,
                authClient = zefirApplication().authClient
            )
        }
    }
}

fun CreationExtras.zefirApplication() =
    (this[APPLICATION_KEY] as ZefirApplication)
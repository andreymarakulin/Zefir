package com.andmar.zefir.ui.theme.chat

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andmar.zefir.DefaultTextField
import com.andmar.zefir.DefaultTopAppBar
import com.andmar.zefir.ErrorDialog
import com.andmar.zefir.LoadingDialog
import com.andmar.zefir.R
import com.andmar.zefir.ZefirViewModelProvider
import com.andmar.zefir.firebase.FirebaseActions
import kotlinx.serialization.Serializable

@Serializable
data class EditChatNameScreenRoute(val chatId: String)

@Composable
fun EditChatNameScreen(
    viewModel: EditChatNameViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavBack: () -> Unit
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.edit_chat_name_title),
                onClickNavIcon = { onNavBack() }
            )
        },
    ) { innerPadding ->
        EditChatNameBody(
            innerPaddingValues = innerPadding,
            editChatNameUiState = viewModel.editChatNameUiState,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun EditChatNameBody(
    innerPaddingValues: PaddingValues,
    editChatNameUiState: EditChatNameUiState,
    onNavBack: () -> Unit,
    onActions: (EditChatNameActions) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            Text(
                text = stringResource(R.string.edit_chat_name_text),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )
        }

        item {
            EntryChatNameForm(
                chatDetails = editChatNameUiState.chatDetails
            ) { onActions(EditChatNameActions.UpdateEditChatNameUiState(it)) }
        }

        item {
            Button(
                onClick = { onActions(EditChatNameActions.Edit) },
                enabled = editChatNameUiState.isAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.edit_title)) }
        }
    }

    when(editChatNameUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = editChatNameUiState.firebaseActions.message
            ) { onActions(EditChatNameActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}

@Composable
fun EntryChatNameForm(
    chatDetails: ChatDetails,
    updateChatDetails: (ChatDetails) -> Unit
) {
    DefaultTextField(
        value = chatDetails.name,
        label = stringResource(R.string.chat_name_label)
    ) { updateChatDetails(chatDetails.copy(name = it)) }
}

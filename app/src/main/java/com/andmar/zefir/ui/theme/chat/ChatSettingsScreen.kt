package com.andmar.zefir.ui.theme.chat

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andmar.zefir.DefaultTopAppBar
import com.andmar.zefir.ErrorDialog
import com.andmar.zefir.LoadingDialog
import com.andmar.zefir.R
import com.andmar.zefir.ZefirViewModelProvider
import com.andmar.zefir.firebase.FirebaseActions
import kotlinx.serialization.Serializable

@Serializable
data class ChatSettingsScreenRoute(val chatId: String)

@Composable
fun ChatSettingsScreen(
    viewModel: ChatSettingsViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavEditChatName: (String) -> Unit,
    onNavAddUser: (String) -> Unit,
    onNavBack: () -> Unit
) {

    val chatDetailsState = viewModel.chatDetailsState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.chat_settings_title),
                onClickNavIcon = { onNavBack() }
            )
        },
    ) { innerPadding ->
        ChatSettingsBody(
            innerPaddingValues = innerPadding,
            chatSettingsUiState = viewModel.chatSettingsUiState,
            chatDetailsState = chatDetailsState.value,
            onClickEditChatName = { onNavEditChatName(it) },
            onClickAddUser = { onNavAddUser(it) },
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun ChatSettingsBody(
    innerPaddingValues: PaddingValues,
    chatSettingsUiState: ChatSettingsUiState,
    chatDetailsState: ChatDetailsState,
    onClickEditChatName: (String) -> Unit,
    onClickAddUser: (String) -> Unit,
    onNavBack: () -> Unit,
    onActions: (ChatSettingsActions) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(innerPaddingValues)
    ) {

        item {
            ChatNameCard(
                chatName = chatDetailsState.chatDetails.name
            ) {  onClickEditChatName(chatDetailsState.chatDetails.id) }
        }

        items(chatDetailsState.chatDetails.usersId) { userId ->
            ChatUserCard(
                userId = userId
            ) { onActions(ChatSettingsActions.DeleteUser(userId)) }
        }

        item {
            Button(
                onClick = { onClickAddUser(chatDetailsState.chatDetails.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.add_user_title)) }
        }

        item {
            ChatActionsCard(
                onClickOut = { onActions(ChatSettingsActions.Out) },
                onClickDelete = { onActions(ChatSettingsActions.DeleteChat) }
            )
        }
    }

    when(chatSettingsUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = chatSettingsUiState.firebaseActions.message
            ) { onActions(ChatSettingsActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}

@Composable
fun ChatNameCard(
    chatName: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = chatName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(10.dp)
            )
            IconButton(
                onClick = { onClick() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.edit),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ChatUserCard(
    userId: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier.padding(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = userId,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .padding(10.dp)
            )
            IconButton(
                onClick = { onClick() },
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ChatActionsCard(
    onClickOut: () -> Unit,
    onClickDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {

        Text(
            text = stringResource(R.string.chat_actions_title),
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .padding(10.dp)
        )
        Button(
            onClick = { onClickOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(vertical = 5.dp)
        ) { Text(stringResource(R.string.chat_out_title)) }
        Button(
            onClick = { onClickDelete() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(vertical = 5.dp)
        ) { Text(stringResource(R.string.delete_title)) }
    }
}
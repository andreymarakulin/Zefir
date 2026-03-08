package com.andmar.zefir.ui.theme.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.andmar.zefir.DefaultFloatingActionButton
import com.andmar.zefir.DefaultTopAppBar
import com.andmar.zefir.R
import com.andmar.zefir.ZefirViewModelProvider
import com.andmar.zefir.ui.theme.chat.ChatDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.io.encoding.Base64

@Serializable
object HomeScreenRoute

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavSignIn: () -> Unit,
    onNavAccount: () -> Unit,
    onNavAddChat: () -> Unit,
    onNavChat: (String) -> Unit
) {
    val authState = viewModel.authState.collectAsStateWithLifecycle()
    val chatDetailsListState = viewModel.chatDetailsListState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.app_name),
                actionsIcon = R.drawable.account_circle,
                onActions = { onNavAccount() }
            )
        },
        floatingActionButton = {
            DefaultFloatingActionButton { onNavAddChat() }
        }
    ) { innerPadding ->
        HomeBody(
            innerPaddingValues = innerPadding,
            chatDetailsListState = chatDetailsListState.value,
            onClickChatCard = { onNavChat(it) }
        )

        LaunchedEffect(authState.value.authState) {
            if (!authState.value.authState) {
                delay(500)
                onNavSignIn()
            }
        }
    }
}

@Composable
fun HomeBody(
    innerPaddingValues: PaddingValues,
    chatDetailsListState: ChatDetailsListState,
    onClickChatCard: (String) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        items(chatDetailsListState.chatDetailsList) { chatDetails ->
            ChatDetailsCard(
                chatDetails = chatDetails
            ) { onClickChatCard(it) }
        }

        item {
            Box(
                modifier = Modifier.height(100.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_new_chat_text),
                    modifier = Modifier.padding(10.dp)
                )
            }
        }
    }
}

@Composable
fun ChatDetailsCard(
    chatDetails: ChatDetails,
    onClick: (String) -> Unit
) {

    Card(
        onClick = { onClick(chatDetails.id) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(vertical = 5.dp)
    ) {
        Text(
            text = chatDetails.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(10.dp)
        )
    }
}
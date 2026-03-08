package com.andmar.zefir.ui.theme.chat

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Serializable
data class ChatScreenRoute(val chatId: String)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavChatSettings: (String) -> Unit,
    onNavBack: () -> Unit
) {

    val chatDetailsState = viewModel.chatDetailsState.collectAsStateWithLifecycle()
    val messageDetailsListState = viewModel.messageDetailsListState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = chatDetailsState.value.chatDetails.name,
                navIcon = R.drawable.arrow_back,
                navDes = null,
                onClickNavIcon = { onNavBack() },
                actionsIcon = R.drawable.more_vert,
                actionsDes = null,
                onActions = { onNavChatSettings(viewModel.chatId) }
            )
        },
    ) { innerPadding ->
        ChatBody(
            innerPaddingValues = innerPadding,
            messageDetailsListState = messageDetailsListState.value,
            chatUiState = viewModel.chatUiState,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun ChatBody(
    state: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope(),
    innerPaddingValues: PaddingValues,
    messageDetailsListState: MessageDetailsListState,
    chatUiState: ChatUiState,
    onNavBack: () -> Unit,
    onActions: (ChatActions) -> Unit
) {

    val groupedMessageList = remember(messageDetailsListState.messageDetailsList) {
        val sortedMessage = messageDetailsListState.messageDetailsList.sortedBy { it.time }
        groupMessagesByDate(sortedMessage)
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {
        LazyColumn(
            state = state,
            modifier = Modifier
                .weight(1f)
        ) {

            groupedMessageList.forEach { time, messageForTime ->
                stickyHeader {
                    TimeHeader(
                        time = time,
                        messageForTime = messageForTime
                    )
                }
                scope.launch { state.animateScrollToItem(state.layoutInfo.totalItemsCount) }

                items(messageForTime) { messageDetails ->
                    MessageDetailsCard(
                        modifier = Modifier.animateItem(),
                        messageDetails = messageDetails
                    )
                }
            }
        }
        MessageTextField(
            value = chatUiState.messageDetails.message,
            placeholder = stringResource(R.string.message_title),
            enabled = chatUiState.isAction,
            onActions = { onActions(ChatActions.Send) }
        ) { onActions(ChatActions.UpdateMessage(it)) }
    }

    when(chatUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = chatUiState.firebaseActions.message
            ) { onActions(ChatActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}

@Composable
fun TimeHeader(
    time: String,
    messageForTime: List<MessageDetails>
) {
    val headerText = remember(time, messageForTime) {
        if (messageForTime.isNotEmpty()) {
            formatDateHeader(messageForTime.first().time)
        } else ""
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
        ) {
            Text(
                text = headerText,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}

@Composable
fun MessageDetailsCard(
    modifier: Modifier,
    messageDetails: MessageDetails
) {

    Row(
        horizontalArrangement = if(messageDetails.isMyMessage) Arrangement.End else Arrangement.Start,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = modifier
                .widthIn(max = 300.dp)
                .padding(5.dp)
        ) {
            if (!messageDetails.isMyMessage) {
                Text(
                    text = messageDetails.userName,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(3.dp)
                )
            }
            Text(
                text = messageDetails.message,
                modifier = Modifier
                    .padding(5.dp)
            )
        }
    }
}


@Composable
fun MessageTextField(
    value: String,
    placeholder: String,
    enabled: Boolean,
    onActions: () -> Unit = {},
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(20.dp),
        maxLines = 7,
        keyboardActions = KeyboardActions { onActions() },
        trailingIcon = {
            IconButton(
                onClick = onActions,
                enabled = enabled
            ) {
                Icon(
                    painter = painterResource(R.drawable.send),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(vertical = 5.dp)
    )
}


// Функция для форматирования даты
fun formatDateHeader(timestamp: Long): String {
    val date = Date(timestamp)
    val calendar = Calendar.getInstance()
    calendar.time = date

    val now = Calendar.getInstance()

    return when {
        isSameDay(calendar, now) -> "Сегодня"
        isSameDay(calendar, now.apply { add(Calendar.DAY_OF_YEAR, -1) }) -> "Вчера"
        else -> {
            val format = SimpleDateFormat("d MMMM", Locale.getDefault())
            format.format(date)
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

// Группировка сообщений по дате
fun groupMessagesByDate(messages: List<MessageDetails>): Map<String, List<MessageDetails>> {
    return messages.groupBy { message ->
            val date = Date(message.time)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            format.format(date)
        }
}
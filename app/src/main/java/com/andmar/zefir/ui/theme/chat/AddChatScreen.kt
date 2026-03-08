package com.andmar.zefir.ui.theme.chat

import androidx.compose.foundation.layout.Column
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
object AddChatScreenRoute

@Composable
fun AddChatScreen(
    viewModel: AddChatViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavBack: () -> Unit
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.add_chat_title),
                onClickNavIcon = { onNavBack() }
            )
        }
    ) { innerPadding ->
        AddChatBody(
            innerPaddingValues = innerPadding,
            addChatUiState = viewModel.addChatUiState,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }

        when(viewModel.addChatUiState.firebaseActions) {
            is FirebaseActions.Loading -> {
                LoadingDialog()
            }
            is FirebaseActions.Error -> {
                ErrorDialog(
                    title = "Error!",
                    text = (viewModel.addChatUiState.firebaseActions as FirebaseActions.Error).message
                ) { viewModel.onActions(AddChatActions.Error) }
            }
            is FirebaseActions.Success -> { onNavBack() }
            is FirebaseActions.None -> {}
        }
    }
}

@Composable
fun AddChatBody(
    innerPaddingValues: PaddingValues,
    addChatUiState: AddChatUiState,
    onNavBack: () -> Unit,
    onActions: (AddChatActions) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            Text(
                text = stringResource(R.string.add_chat_text),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }

        item {
            AddChatForm(
                addChatDetails = addChatUiState.addChatDetails
            ) { onActions(AddChatActions.UpdateAddChatDetails(it)) }
        }
        item {
            Button(
                onClick = { onActions(AddChatActions.AddChat) },
                enabled = addChatUiState.isAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.add_chat_title)) }
        }
    }
}

@Composable
fun AddChatForm(
    addChatDetails: AddChatDetails,
    updateAddChatDetails: (AddChatDetails) -> Unit
) {

    Column() {

        DefaultTextField(
            value = addChatDetails.name,
            label = stringResource(R.string.name_label)
        ) { updateAddChatDetails(addChatDetails.copy(name = it)) }
        DefaultTextField(
            value = addChatDetails.uid,
            label = stringResource(R.string.user_id_label)
        ) { updateAddChatDetails(addChatDetails.copy(uid = it)) }
    }
}
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
data class AddUserScreenRoute(val chatId: String)

@Composable
fun AddUserScreen(
    viewModel: AddUserViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavBack: () -> Unit
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.add_user_title),
                onClickNavIcon = { onNavBack() }
            )
        },
    ) { innerPadding ->
        AddUserBody(
            innerPaddingValues = innerPadding,
            addUserUiState = viewModel.addUserUiState,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun AddUserBody(
    innerPaddingValues: PaddingValues,
    addUserUiState: AddUserUiState,
    onNavBack: () -> Unit,
    onActions: (AddUserActions) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            Text(
                text = stringResource(R.string.add_user_text),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(20.dp)
            )
        }

        item {
            DefaultTextField(
                value = addUserUiState.userId,
                label = stringResource(R.string.user_id_label)
            ) { onActions(AddUserActions.UpdateAddUserUiState(it)) }
        }

        item {
            Button(
                onClick = { onActions(AddUserActions.Add) },
                enabled = addUserUiState.isAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.add_user_title)) }
        }
    }

    when(addUserUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = addUserUiState.firebaseActions.message
            ) { onActions(AddUserActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}
package com.andmar.zefir.ui.theme.account

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import com.andmar.zefir.ui.theme.chat.EditChatNameActions
import kotlinx.serialization.Serializable

@Serializable
object CreateUserScreenRoute

@Composable
fun CreateUserScreen(
    viewModel: CreateUserViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavBack: () -> Unit
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.create_user_title),
                onClickNavIcon = { onNavBack() }
            )
        }
    ) { innerPadding ->
        CreateUserBody(
            innerPaddingValues = innerPadding,
            createUserUiState = viewModel.createUserUiState,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun CreateUserBody(
    innerPaddingValues: PaddingValues,
    createUserUiState: CreateUserUiState,
    onNavBack: () -> Unit,
    onActions: (CreateUserActions) -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            Text(
                text = stringResource(R.string.create_user_text),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }
        item {
            NameForm(
                authDetails = createUserUiState.authDetails
            ) { onActions(CreateUserActions.UpdateCreateUserUiState(it)) }
        }
        item {
            AuthDetailsForm(
                authDetails = createUserUiState.authDetails
            ) { onActions(CreateUserActions.UpdateCreateUserUiState(it)) }
        }
        item {
            AgainPasswordForm(
                authDetails = createUserUiState.authDetails
            ) { onActions(CreateUserActions.UpdateCreateUserUiState(it)) }
        }
        item {
            Button(
                onClick = { onActions(CreateUserActions.CreateUser) },
                enabled = createUserUiState.isAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.create_user_title)) }
        }
    }

    when(createUserUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = createUserUiState.firebaseActions.message
            ) { onActions(CreateUserActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}


@Composable
fun NameForm(
    authDetails: AuthDetails,
    updateAuthDetails: (AuthDetails) -> Unit
) {

    DefaultTextField(
        value = authDetails.name,
        label = stringResource(R.string.name_label)
    ) { updateAuthDetails(authDetails.copy(name = it)) }
}

@Composable
fun AgainPasswordForm(
    authDetails: AuthDetails,
    updateAuthDetails: (AuthDetails) -> Unit
) {

    DefaultTextField(
        value = authDetails.againPassword,
        label = stringResource(R.string.again_password_label)
    ) { updateAuthDetails(authDetails.copy(againPassword = it)) }
}
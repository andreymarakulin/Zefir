package com.andmar.zefir.ui.theme.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
object SignInScreenRoute

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavCreateUser: () -> Unit,
    onNavHome: () -> Unit,
    onNavBack: () -> Unit
) {

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.sign_in_title),
                onClickNavIcon = { onNavBack() }
            )
        }
    ) { innerPadding ->
        SignInBody(
            innerPaddingValues = innerPadding,
            signInUiState = viewModel.signInUiState,
            onClickCreateUser = { onNavCreateUser() },
            onNavHome = onNavHome,
            onNavBack = onNavBack
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun SignInBody(
    innerPaddingValues: PaddingValues,
    signInUiState: SignInUiState,
    onClickCreateUser: () -> Unit,
    onNavBack: () -> Unit,
    onNavHome: () -> Unit,
    onActions: (SignInActions) -> Unit
) {

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            Text(
                text = stringResource(R.string.sign_in_text),
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(10.dp)
            )
        }

        item {
            AuthDetailsForm(
                authDetails = signInUiState.authDetails
            ) { onActions(SignInActions.UpdateSignInUiState(it)) }
        }
        item {
            TextButton(
                onClick = { onClickCreateUser() },
                modifier = Modifier.padding(10.dp)
            ) { Text(stringResource(R.string.create_account_title)) }
        }
        item {
            Button(
                onClick = { onActions(SignInActions.SignIn) },
                enabled = signInUiState.isAction,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) { Text(stringResource(R.string.sign_in_title)) }
        }
    }

    when(signInUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = signInUiState.firebaseActions.message
            ) { onActions(SignInActions.Error) }
        }
        is FirebaseActions.Success -> { onNavHome() }
        is FirebaseActions.None -> {}
    }
}

@Composable
fun AuthDetailsForm(
    authDetails: AuthDetails,
    updateAuthDetails: (AuthDetails) -> Unit
) {

    Column() {

        DefaultTextField(
            value = authDetails.email,
            label = stringResource(R.string.email_label)
        ) { updateAuthDetails(authDetails.copy(email = it)) }
        DefaultTextField(
            value = authDetails.password,
            label = stringResource(R.string.password_label)
        ) { updateAuthDetails(authDetails.copy(password = it)) }
    }
}
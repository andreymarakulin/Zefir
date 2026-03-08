package com.andmar.zefir.ui.theme.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
object AccountScreenRoute

@Composable
fun AccountScreen(
    viewModel: AccountViewModel = viewModel(factory = ZefirViewModelProvider.Factory),
    onNavBack: () -> Unit
) {

    val userDetailState = viewModel.userDetailState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            DefaultTopAppBar(
                title = stringResource(R.string.account_title),
                navIcon = R.drawable.arrow_back,
                onClickNavIcon = { onNavBack() }
            )
        }
    ) { innerPadding ->
        AccountBody(
            innerPaddingValues = innerPadding,
            userDetailState = userDetailState.value,
            accountUiState = viewModel.accountUiState,
            onNavBack = onNavBack,
            onClickUpdateName = { viewModel.updateName() }
        ) { viewModel.onActions(it) }
    }
}

@Composable
fun AccountBody(
    innerPaddingValues: PaddingValues,
    userDetailState: UserDetailState,
    accountUiState: AccountUiState,
    onNavBack: () -> Unit,
    onClickUpdateName: () -> Unit,
    onActions: (AccountActions) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPaddingValues)
    ) {

        item {
            UserDetailsCard(
                userDetails = userDetailState.userDetails
            )
        }

        item {
            AccountSettingsCard(
                onClickSignOut = { onActions(AccountActions.SignOut) },
                onClickDelete = {}
            )
        }

        item {
            Column() {
                Text(
                    text = "Для тех у кого был установлен Zefir до выпуска",
                    modifier = Modifier.padding(10.dp)
                )
                Button(
                    onClick = { onClickUpdateName() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text("Обновить имя")
                }
            }
        }
    }

    when(accountUiState.firebaseActions) {
        is FirebaseActions.Loading -> {
            LoadingDialog()
        }
        is FirebaseActions.Error -> {
            ErrorDialog(
                title = "Error!",
                text = accountUiState.firebaseActions.message
            ) { onActions(AccountActions.Error) }
        }
        is FirebaseActions.Success -> { onNavBack() }
        is FirebaseActions.None -> {}
    }
}

@Composable
fun UserDetailsCard(
    userDetails: UserDetails
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = userDetails.name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp)
        )
        Text(
            text = userDetails.email,
            modifier = Modifier.padding(5.dp)
        )
        SelectionContainer() {
            Text(
                text = userDetails.id,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
fun AccountSettingsCard(
    onClickSignOut: () -> Unit,
    onClickDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.account_actions_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(5.dp)
        )
        Button(
            onClick = { onClickSignOut() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) { Text(stringResource(R.string.sign_out_title)) }
        /*
        Button(
            onClick = { onClickDelete() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .padding(vertical = 5.dp)
        ) { Text("Delete account") }

         */
    }
}
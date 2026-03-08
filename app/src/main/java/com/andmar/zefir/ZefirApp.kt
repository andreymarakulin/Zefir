package com.andmar.zefir

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ZefirApp() {
    NavigationClient()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopAppBar(
    title: String,
    navIcon: Int? = null,
    navDes: String? = null,
    onClickNavIcon: () -> Unit = {},
    actionsIcon: Int? = null,
    actionsDes: String? = null,
    onActions: () -> Unit = {}
) {

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            if (navIcon != null) {
                IconButton(
                    onClick = { onClickNavIcon() }
                ) {
                    Icon(
                        painter = painterResource(navIcon),
                        contentDescription = navDes
                    )
                }
            }
        },
        actions = {
            if (actionsIcon != null) {
                IconButton(
                    onClick = { onActions() }
                ) {
                    Icon(
                        painter = painterResource(actionsIcon),
                        contentDescription = actionsDes
                    )
                }
            }
        }
    )
}

@Composable
fun DefaultFloatingActionButton(
    onClick: () -> Unit
) {

    FloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(
            painter = painterResource(R.drawable.add),
            contentDescription = null
        )
    }
}

@Composable
fun DefaultTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        label = { Text(label) },
        maxLines = 1,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .padding(vertical = 5.dp)
    )
}

@Composable
fun LoadingDialog() {

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Card() {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(5.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = "Loading...",
                    modifier = Modifier.padding(3.dp)
                )
            }
        }
    }
}

@Composable
fun ErrorDialog(
    title: String,
    text: String,
    onDismiss: () -> Unit
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            TextButton(
                onDismiss
            ) { Text("OK") }
        }
    )
}
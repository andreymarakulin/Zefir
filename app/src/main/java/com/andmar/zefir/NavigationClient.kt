package com.andmar.zefir

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.andmar.zefir.ui.theme.account.AccountScreen
import com.andmar.zefir.ui.theme.account.AccountScreenRoute
import com.andmar.zefir.ui.theme.account.CreateUserScreen
import com.andmar.zefir.ui.theme.account.CreateUserScreenRoute
import com.andmar.zefir.ui.theme.account.CreateUserViewModel
import com.andmar.zefir.ui.theme.account.SignInScreen
import com.andmar.zefir.ui.theme.account.SignInScreenRoute
import com.andmar.zefir.ui.theme.chat.AddChatScreen
import com.andmar.zefir.ui.theme.chat.AddChatScreenRoute
import com.andmar.zefir.ui.theme.chat.AddUserScreen
import com.andmar.zefir.ui.theme.chat.AddUserScreenRoute
import com.andmar.zefir.ui.theme.chat.ChatScreen
import com.andmar.zefir.ui.theme.chat.ChatScreenRoute
import com.andmar.zefir.ui.theme.chat.ChatSettingsScreen
import com.andmar.zefir.ui.theme.chat.ChatSettingsScreenRoute
import com.andmar.zefir.ui.theme.chat.EditChatNameScreen
import com.andmar.zefir.ui.theme.chat.EditChatNameScreenRoute
import com.andmar.zefir.ui.theme.home.HomeScreen
import com.andmar.zefir.ui.theme.home.HomeScreenRoute

@Composable
fun NavigationClient(
    navController: NavHostController = rememberNavController()
) {

    NavHost(
        navController = navController,
        startDestination = HomeScreenRoute
    ) {

        composable<HomeScreenRoute> {
            HomeScreen(
                onNavSignIn = { navController.navigate(SignInScreenRoute) },
                onNavAccount = { navController.navigate(AccountScreenRoute) },
                onNavAddChat = { navController.navigate(AddChatScreenRoute) },
                onNavChat = { navController.navigate(ChatScreenRoute(it)) }
            )
        }

        composable<ChatScreenRoute> {
            ChatScreen(
                onNavChatSettings = { navController.navigate(ChatSettingsScreenRoute(it)) }
            ) { navController.navigateUp() }
        }

        composable<AddChatScreenRoute> {
            AddChatScreen() { navController.navigateUp() }
        }

        composable<ChatSettingsScreenRoute> {
            ChatSettingsScreen(
                onNavEditChatName = { navController.navigate(EditChatNameScreenRoute(it)) },
                onNavAddUser = { navController.navigate(AddUserScreenRoute(it)) }
            ) { navController.navigateUp() }
        }

        composable<EditChatNameScreenRoute> {
            EditChatNameScreen() { navController.navigateUp() }
        }

        composable<AddUserScreenRoute> {
            AddUserScreen() { navController.navigateUp() }
        }

        composable<AccountScreenRoute> {
            AccountScreen() { navController.navigateUp() }
        }

        composable<SignInScreenRoute> {
            SignInScreen(
                onNavCreateUser = { navController.navigate(CreateUserScreenRoute) },
                onNavHome = { navController.navigate(HomeScreenRoute) }
            ) { navController.navigateUp() }
        }

        composable<CreateUserScreenRoute> {
            CreateUserScreen() { navController.navigateUp() }
        }
    }
}
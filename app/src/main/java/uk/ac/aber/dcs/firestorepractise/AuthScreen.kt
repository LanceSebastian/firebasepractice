package uk.ac.aber.dcs.firestorepractise

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseUser
import uk.ac.aber.dcs.firestorepractise.ui.theme.FirestorePractiseTheme

@Composable
fun TopAuthScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
){
    val user by authViewModel.user.observeAsState()
    val authStatus by authViewModel.authStatus.observeAsState()

    AuthScreen(
        user = user,
        authStatus = authStatus,
        login = { email, password ->
            authViewModel.login(email, password)
        },
        register = { email, password, username ->
            authViewModel.register(email, password, username)
        },
        logout = { authViewModel.logout() },
        goToMain = { navController.navigate(Screen.Main.route) }
    )
}
@Composable
fun AuthScreen(
    user: FirebaseUser? = null,
    authStatus: String? = null,
    login: (String, String) -> Unit,
    register: (String, String, String) -> Unit,
    logout: () -> Unit,
    goToMain: () -> Unit,

) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Automatically navigate when auth state changes
    LaunchedEffect(user) {
        if (user != null) {
            goToMain()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (user != null) "Welcome, ${user.email}" else "Please Log In")

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (user == null) {
            Button(onClick = { login(email, password) }, modifier = Modifier.fillMaxWidth()) {
                Text("Login")
            }

            Button(onClick = { register(email, password, username) }, modifier = Modifier.fillMaxWidth()) {
                Text("Register")
            }
        } else {
            Button(onClick = { logout() }, modifier = Modifier.fillMaxWidth()) {
                Text("Logout")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        authStatus?.let {
            Text(text = it)
        }
    }
}

@Preview (showBackground = true)
@Composable
fun AuthScreenPreview(){
    FirestorePractiseTheme {
        AuthScreen(
            login = {_,_ ->},
            logout = {},
            register = {_,_,_ ->},
            goToMain = {}
        )
    }
}

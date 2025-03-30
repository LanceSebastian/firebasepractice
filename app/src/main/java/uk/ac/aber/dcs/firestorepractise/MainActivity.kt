package uk.ac.aber.dcs.firestorepractise

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.firestorepractise.ui.theme.FirestorePractiseTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val userViewModel: UserViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            FirestorePractiseTheme {
                Navigation(
                    context = this,
                    userViewModel = userViewModel,
                    authViewModel = authViewModel
                )
            }
        }
    }
}

@Composable
fun Navigation(
    context: ComponentActivity,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()
    val user by authViewModel.user.observeAsState() // Observe user state

    // Automatically navigate when auth state changes
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate(Screen.Auth.route) {
                popUpTo(Screen.Main.route) { inclusive = true } // Remove Main from stack
            }
        }
    }

    NavHost(navController = navController, startDestination = if (user == null) Screen.Auth.route else Screen.Main.route){

        /* Auth */
        composable(Screen.Auth.route){
            TopAuthScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        /* Home */
        composable(Screen.Main.route){
            TopMainScreen(
                navController = navController,
                userViewModel = userViewModel,
                authViewModel = authViewModel,
                context = context
            )
        }
    }
}
package uk.ac.aber.dcs.firestorepractise

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import uk.ac.aber.dcs.firestorepractise.ui.theme.FirestorePractiseTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TopMainScreen(
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    navController: NavController,
    context: ComponentActivity? = null
){
    val posts by userViewModel.posts.observeAsState(emptyList())
    val username by authViewModel.username.observeAsState("")
    MainScreen(
        addUser = { user, thisContext ->
            userViewModel.addUser(user, thisContext)
        },
        addPost = { username, post, thisContext ->
            userViewModel.addPost(username, post, thisContext)
        },
        getUser = { username, function ->
            userViewModel.getUser(username){ user ->
                function(user)
            }
        },
        getPostsFromUser = { username, function ->
            userViewModel.getPostsFromUser(username){ postList ->
                function(postList)
            }
        },
        listenForPosts = { username ->
            userViewModel.listenForPosts(username)
        },
        logOut = { authViewModel.logout() },
        username = username,
        posts = posts,
        context = context
    )
}

@Composable
fun MainScreen(
    addUser: (User, Context) -> Unit,
    addPost: (String, Post, Context) -> Unit,
    getUser: (String, (User?) -> Unit) -> Unit,
    getPostsFromUser: (String, (List<Post>) -> Unit) -> Unit,
    listenForPosts: (String) -> Unit,
    logOut: () -> Unit,
    username: String? = "",
    posts: List<Post> = emptyList(),
    context: ComponentActivity? = null
){
    var displayUser: User? by remember { mutableStateOf(null) }

    var usernameText: String by remember { mutableStateOf("") }
    var searchUserText: String by remember { mutableStateOf("") }
    var ageText: String by remember { mutableStateOf("") }
    val ageInt = ageText.toIntOrNull() ?: 0
    var titleText: String by remember { mutableStateOf("") }
    var contentText: String by remember { mutableStateOf("") }

    val user = User(username = usernameText, age = ageInt)
    val post = Post(title = titleText, content = contentText)

    // Listen for posts in real-time when the user is found
    LaunchedEffect(displayUser?.username) {
        displayUser?.username?.let{ username ->
            listenForPosts(username)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(8.dp))
            Text("Hello, ${username ?: "how are you?"}")
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    Text("User")

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = usernameText,
                        onValueChange = { usernameText = it },
                        label = { Text("Username") }
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = ageText,
                        onValueChange = { ageText = it },
                        label = { Text("Age") }
                    )

                    // Add user to Firestore
                    Button(onClick = {
                        if (context != null) addUser(user, context)
                    }) {
                        Text("Add User")
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f).padding(8.dp)
                ) {
                    Text("Post")

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Post: Title") }
                    )

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        value = contentText,
                        onValueChange = { contentText = it },
                        label = { Text("Post: Content") }
                    )

                    Button(onClick = {
                        if (context != null) addPost(searchUserText, post, context)
                    }) {
                        Text("Add Post to User")
                    }
                }
            }

            TextField(
                value = searchUserText,
                onValueChange = { searchUserText = it },
                label = { Text("Search Username") }
            )

            // Get user from Firestore
            Button(onClick = {
                getUser(searchUserText) { fetchedUser ->
                    if (fetchedUser != null) {
                        displayUser = fetchedUser
                    } else {
                        displayUser = null
                        context?.let {
                            Toast.makeText(it, "User not found!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Text("Get User from Firestore")
            }

            if (displayUser != null) {
                Text("User: ${displayUser!!.username} is ${displayUser!!.age} years old.")
                Button(onClick = { displayUser?.username?.let { listenForPosts(it) } },
                    enabled = displayUser != null
                ) {
                    Text("Get Posts from ${displayUser?.username ?: "User"}")
                }
            }

            LazyColumn {
                posts.forEach { post ->
                    item {
                        Column {
                            Text("${post.title}: ${convertTimestampToDate(post.timestamp)}")
                            Text(post.content)
                        }
                    }
                }
            }
        }

        // Button at the bottom
        Button(
            onClick = { logOut() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Log Out")
        }
    }
}

fun convertTimestampToDate(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .withLocale(Locale.getDefault())
        .withZone(ZoneId.systemDefault())

    return formatter.format(Instant.ofEpochMilli(timestamp))
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview(){
    FirestorePractiseTheme {
        MainScreen(addPost = {_, _,_ -> },
            addUser = {_,_ ->},
            getUser = {_,_ ->},
            getPostsFromUser = {_,_ ->},
            listenForPosts = {_ ->},
            logOut = {}
        )
    }
}

class FakeActivity: ComponentActivity()
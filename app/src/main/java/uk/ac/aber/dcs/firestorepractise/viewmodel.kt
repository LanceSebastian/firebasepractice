package uk.ac.aber.dcs.firestorepractise

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val firestoreRepository = FirestoreRepository()

    // LiveData or State for posts
    private var _posts = MutableLiveData<List<Post>>()
    var posts: LiveData<List<Post>> = _posts

    // Function to add user to Firestore
    fun addUser(user: User, context: Context) {
        viewModelScope.launch {
            if (firestoreRepository.getUser(user.username) != null){
                Toast.makeText(context, "Username already exists", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val isSuccess = firestoreRepository.addUser(user)
            if (isSuccess) {
                Toast.makeText(context, "User added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error adding user!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addPost(username: String, post: Post, context: Context){
        viewModelScope.launch {
            val isSuccess = firestoreRepository.addUserPost(username, post)
            if (isSuccess) {
                Toast.makeText(context, "Post added successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Error adding post!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    // Function to get user from Firestore
    fun getUser(username: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            val user = firestoreRepository.getUser(username)
            onResult(user)
        }
    }
    fun getPostsFromUser(username: String, onResult: (List<Post>) -> Unit){
        viewModelScope.launch {
            val user = firestoreRepository.getUserPosts(username)
            onResult(user)
        }
    }

    // Start listening for posts from a specific user
    fun listenForPosts(username: String) {
        firestoreRepository.listenForPosts(username) { posts ->
            _posts.postValue(posts)  // Update the LiveData when new posts are received
        }
    }

}
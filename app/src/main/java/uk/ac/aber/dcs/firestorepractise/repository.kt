package uk.ac.aber.dcs.firestorepractise

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // Add a new User to Firestore
    suspend fun addUser(user: User): Boolean {
        return try {
            db.collection("users")
                .document(user.username)
                .set(user)
                .await()  // Use await to make it a coroutine (suspend function)
            true
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error adding user to Firestore: ${e.message}", e)
            false
        }
    }

    suspend fun addUserPost(username: String, post: Post): Boolean{
        return try {
            db.collection("users")
                .document(username)
                .collection("posts")
                .document(post.timestamp.toString()) // Make postId the timestamp
                .set(post)
                .await()
            Log.d("Firestore", "Post added successfully")
            true
        } catch(e: Exception) {
            Log.e("Firestore", "Error adding post: ${e.message}", e)
            false
        }
    }

    // Get User from Firestore
    suspend fun getUser(username: String): User? {
        return try {
            val documentSnapshot = db.collection("users").document(username).get().await()
            documentSnapshot.toObject(User::class.java)
        } catch (e: Exception) {
            null  // Return null if the user is not found or error occurs
        }
    }

    suspend fun getUserPosts(username: String): List<Post> {
        return try{
            val snapshot = db.collection("users")
                .document(username)
                .collection("posts")
                .get()
                .await()
            if (snapshot.isEmpty){
                Log.e("Firestore", "No posts found for $username")
                emptyList()
            } else {
                snapshot.toObjects(Post::class.java)
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Error getting Posts: ${e.message}", e)
            emptyList()
        }
    }

    // Function to listen for posts in real-time for a specific user
    fun listenForPosts(username: String, onResult: (List<Post>) -> Unit): ListenerRegistration {
        return db.collection("users")
            .document(username)
            .collection("posts")
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    Log.e("Firestore", "Error fetching posts: ${exception.message}")
                    return@addSnapshotListener
                }

                // Convert Firestore documents to Post objects
                val posts = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(Post::class.java)  // This returns a nullable Post? object
                } ?: emptyList()

                onResult(posts)  // Send the filtered posts back to the caller via the callback
            }
    }
}
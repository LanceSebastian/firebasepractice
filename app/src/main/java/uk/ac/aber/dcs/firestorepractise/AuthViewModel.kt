package uk.ac.aber.dcs.firestorepractise

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    init {
        _user.value = auth.currentUser // Check if user is already logged in
    }

    fun register(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _authStatus.value = "Registration successful!"
                } else {
                    _authStatus.value = "Registration failed: ${task.exception?.message}"
                }
            }
    }

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _user.value = auth.currentUser
                    _authStatus.value = "Login successful!"
                } else {
                    _authStatus.value = "Login failed: ${task.exception?.message}"
                }
            }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
        _authStatus.value = "User logged out!"
    }

    fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authStatus.value = "Password reset email sent!"
                } else {
                    _authStatus.value = "Failed to send reset email: ${task.exception?.message}"
                }
            }
    }

    fun deleteUser() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = null
                _authStatus.value = "User deleted successfully!"
            } else {
                _authStatus.value = "Failed to delete user: ${task.exception?.message}"
            }
        }
    }
}
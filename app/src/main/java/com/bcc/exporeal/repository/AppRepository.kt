package com.bcc.exporeal.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bcc.exporeal.model.UserModel
import com.bcc.exporeal.util.GetResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository @Inject constructor(
    context: Context,
    private val realtimeDb: FirebaseDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val getResponse: GetResponse
) {
    val datastore = context.datastore

    // (DATASTORE) SAVE landingState
    suspend fun savePassingLandingScreenState(hasPassed: Boolean) {
        datastore.edit {
            it[booleanPreferencesKey("HAS_PASSED_LANDING")] = hasPassed
        }
    }

    // (DATASTORE) GET landingState
    val hasPassedLandingScreen = datastore.data.map {
        it[booleanPreferencesKey("HAS_PASSED_LANDING")] ?: false
    }

    // (AUTH) CHECK isLoggedIn
    fun isLoggedIn() = (auth.currentUser != null)

    // (AUTH) Login with email-password
    fun loginWithEmailPassword(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        auth
            .signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }
    }

    // (AUTH & FIRESTORE) Register with email password
    fun registerWithEmailPassword(
        email: String,
        password: String,
        fullName: String,
        phoneNum: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firestoreDb
                    .collection("user")
                    .document(it.user?.uid ?: "")
                    .set(
                        UserModel(
                            uid = it.user?.uid,
                            name = fullName,
                            email = it.user?.email,
                            phone_num = phoneNum,
                            profile_pic = ""
                        )
                    )
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailed() }
            }
            .addOnFailureListener { onFailed() }
    }

    // (STORAGE & FIRESTORE) Save Profile Pict
    fun saveProfilePicture(
        uri: Uri,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        onProgress: (transferred:Long, total:Long) -> Unit
    ) {
        // Save to storage
        storage
            .reference
            .child("profile_pic/${auth.currentUser?.uid}.png")
            .putFile(uri)
            .addOnProgressListener {
                onProgress(it.bytesTransferred, it.totalByteCount)
            }
            .addOnSuccessListener {
                // Get image URL
                it.storage
                    .child("profile_pic/${auth.currentUser?.uid}.png")
                    .downloadUrl
                    .addOnSuccessListener {
                        // Save to firestore
                        firestoreDb
                            .collection("user")
                            .document(auth.currentUser?.uid ?: "")
                            .set(it.toString())
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailed() }
                    }.addOnFailureListener {
                        onFailed()
                    }
            }
            .addOnFailureListener {
                onFailed()
            }
    }
}
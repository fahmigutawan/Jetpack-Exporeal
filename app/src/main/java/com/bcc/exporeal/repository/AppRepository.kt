package com.bcc.exporeal.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.bcc.exporeal.model.*
import com.bcc.exporeal.util.GetResponse
import com.bcc.exporeal.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppRepository @Inject constructor(
    context: Context,
    private val realtimeDb: FirebaseDatabase,
    private val firestoreDb: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth,
    private val getResponse: GetResponse,
    private val httpClient: HttpClient
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

    // (AUTH) GET current UID
    fun getCurrentUid() = auth.currentUser?.uid ?: ""

    // (AUTH) Login with email-password
    fun loginWithEmailPassword(
        email: String, password: String, onSuccess: () -> Unit, onFailed: () -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }
    }

    // (AUTH) Logout
    suspend fun logout(delay: Long = 2000L, afterDelay: () -> Unit) {
        auth.signOut()
        kotlinx.coroutines.delay(delay)
        afterDelay()
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
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
            firestoreDb.collection("user").document(it.user?.uid ?: "").set(
                UserModel(
                    uid = it.user?.uid,
                    name = fullName,
                    email = it.user?.email,
                    phone_num = phoneNum,
                    profile_pic = ""
                )
            ).addOnSuccessListener { onSuccess() }.addOnFailureListener { onFailed() }
        }.addOnFailureListener { onFailed() }
    }

    // (STORAGE & FIRESTORE) Save Profile Pict
    fun saveProfilePicture(
        uri: Uri,
        user: UserModel,
        onSuccess: () -> Unit,
        onFailed: () -> Unit,
        onProgress: (transferred: Long, total: Long) -> Unit
    ) {
        // Save to storage
        storage.reference.child("profile_pic/${auth.currentUser?.uid}.png").putFile(uri)
            .addOnProgressListener {
                onProgress(it.bytesTransferred, it.totalByteCount)
            }.addOnSuccessListener {
                // Get image URL
                storage.reference.child("profile_pic/${auth.currentUser?.uid}.png").downloadUrl.addOnSuccessListener {
                    // Save to firestore
                    firestoreDb.collection("user").document(auth.currentUser?.uid ?: "").set(
                        user.copy(
                            profile_pic = it.toString()
                        )
                    ).addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailed() }
                }.addOnFailureListener {
                    onFailed()
                }
            }.addOnFailureListener {
                onFailed()
            }
    }

    // (FIRESTORE) GET random key from specific collection
    fun getRandomKey(collection_name: String) =
        firestoreDb.collection(collection_name).document().id

    // (FIRESTORE) GET own userInfo
    fun getOwnUserInfo(): Flow<Resource<UserModel>?> = getResponse.getFirestoreResponse {
        firestoreDb.collection("user").document(auth.currentUser?.uid ?: "").get()
    }

    // (FIRESTORE) GET userInfo by UID
    fun getUserInfoByUid(uid: String): Flow<Resource<UserModel>?> =
        getResponse.getFirestoreResponse {
            firestoreDb.collection("user").document(uid).get()
        }

    // (FIRESTORE) GET banner
    fun getHomeBanner(): Flow<Resource<List<BannerModel>>?> = getResponse.getFirestoreListResponse {
        firestoreDb.collection("banner")
            .orderBy("url", com.google.firebase.firestore.Query.Direction.ASCENDING).get()
    }

    // (FIRESTORE) GET category
    fun getCategories(): Flow<Resource<List<CategoryModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("category")
                .orderBy("count", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .get()
        }

    // (FIRESTORE) GET category by category_id
    fun getCategoryByCategoryId(category_id: String): Flow<Resource<CategoryModel>?> =
        getResponse.getFirestoreResponse(timeDelay = 0L) {
            firestoreDb.collection("category").document(category_id).get()
        }

    // (FIRESTORE) GET list of 10 top products
    fun getTop10Product(): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product")
                .orderBy("product_id", com.google.firebase.firestore.Query.Direction.ASCENDING)
                .limit(10).get()
        }

    // (FIRESTORE) GET list of 2 top permintaan
    fun getTop2Permintaan(): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_id", com.google.firebase.firestore.Query.Direction.ASCENDING
            ).limit(2).get()
        }

    // (FIRESTORE) GET list of product pictures
    fun getProductPicturesByProductId(product_id: String): Flow<Resource<List<ProductPictureModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product_picture")
                .whereGreaterThanOrEqualTo("product_id", product_id)
                .whereLessThanOrEqualTo("product_id", "$product_id\uF7FF").get()
        }

    // (FIRESTORE) GET list of first 8 product
    fun getFirstProductsWithNoFilter(): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product").orderBy(
                "product_count", com.google.firebase.firestore.Query.Direction.ASCENDING
            ).limit(8).get()
        }

    // (FIRESTORE) GET list of next 8 products
    fun getNextProductsWithNoFilter(lastVisiblePostCount: Int): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product").orderBy(
                "product_count", com.google.firebase.firestore.Query.Direction.ASCENDING
            ).startAfter(lastVisiblePostCount).limit(8).get()
        }

    // (FIRESTORE) GET list of first 8 permintaan
    fun getFirstPermintaanWithNoFilter(): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_count", com.google.firebase.firestore.Query.Direction.ASCENDING
            ).limit(8).get()
        }

    // (FIRESTORE) GET list of next 8 permintaan
    fun getNextPermintaanWithNoFilter(lastVisiblePermintaanCount: Int): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_count", com.google.firebase.firestore.Query.Direction.ASCENDING
            ).startAfter(lastVisiblePermintaanCount).limit(8).get()
        }

    // (FIRESTORE) SAVE bisnis register
    fun saveBusinessRegistorToFirestore(
        body: BusinessModel,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        firestoreDb
            .collection("business")
            .document(body.business_id ?: "")
            .set(body)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailed() }
    }

    fun getBusinessByUid(): Flow<Resource<List<BusinessModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("business")
                .whereGreaterThanOrEqualTo("uid", getCurrentUid())
                .whereLessThanOrEqualTo("uid", "${getCurrentUid()}\uF7FF")
                .get()
        }
}
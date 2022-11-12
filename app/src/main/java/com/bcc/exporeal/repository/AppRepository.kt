package com.bcc.exporeal.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bcc.exporeal.model.*
import com.bcc.exporeal.util.FcmRoutes
import com.bcc.exporeal.util.GetResponse
import com.bcc.exporeal.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage
import com.google.firebase.storage.FirebaseStorage
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
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
    private val httpClient: HttpClient,
    private val messaging: FirebaseMessaging
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

    // (DATASTORE) SAVE is new token available status
    suspend fun saveIsNewFcmTokenAvailable(available: Boolean) {
        datastore.edit {
            it[booleanPreferencesKey("IS_NEW_TOKEN_AVAILABLE")] = available
        }
    }

    // (DATASTORE) GET is new token available status
    val isNewFcmTokenAvailable = datastore.data.map {
        it[booleanPreferencesKey("IS_NEW_TOKEN_AVAILABLE")] ?: false
    }

    // (DATASTORE) SAVE fcm token
    suspend fun saveFcmToken(token: String) {
        datastore.edit {
            it[stringPreferencesKey("FCM_TOKEN")] = token
        }
    }

    // (DATASTORE) GET fcm token
    val fcmToken = datastore.data.map {
        it[stringPreferencesKey("FCM_TOKEN")] ?: ""
    }

    // (MESSAGING) get new token
    fun getFcmToken(onSuccess: (String) -> Unit) {
        messaging.token.addOnSuccessListener { onSuccess(it) }
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
        firestoreDb
            .collection("fcm_token")
            .document(getCurrentUid())
            .delete()

        auth.signOut()
        saveIsNewFcmTokenAvailable(false)
        saveFcmToken("")
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
    fun getOwnUserInfo(delay: Long = 2500L): Flow<Resource<UserModel>?> = getResponse.getFirestoreResponse(timeDelay = delay) {
        firestoreDb.collection("user").document(auth.currentUser?.uid ?: "").get()
    }

    // (FIRESTORE) GET userInfo by UID
    fun getUserInfoByUid(uid: String, delay: Long = 2500L): Flow<Resource<UserModel>?> =
        getResponse.getFirestoreResponse(timeDelay = delay) {
            firestoreDb
                .collection("user")
                .document(uid)
                .get()
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
                .orderBy("product_id", Query.Direction.DESCENDING)
                .limit(10).get()
        }

    // (FIRESTORE) GET list of 2 top permintaan
    fun getTop2Permintaan(): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_id", Query.Direction.DESCENDING
            ).limit(2).get()
        }

    // (FIRESTORE) GET list of product pictures
    fun getProductPicturesByProductId(product_id: String): Flow<Resource<List<ProductPictureModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product_images")
                .whereGreaterThanOrEqualTo("product_id", product_id)
                .whereLessThanOrEqualTo("product_id", "$product_id\uF7FF").get()
        }

    // (FIRESTORE) GET list of first 8 product
    fun getFirstProductsWithNoFilter(): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product").orderBy(
                "product_count", Query.Direction.DESCENDING
            ).limit(8).get()
        }

    // (FIRESTORE) GET list of next 8 products
    fun getNextProductsWithNoFilter(lastVisiblePostCount: Int): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("product").orderBy(
                "product_count", Query.Direction.DESCENDING
            ).startAfter(lastVisiblePostCount).limit(8).get()
        }

    // (FIRESTORE) GET list of first 8 product
    fun getFirstProductsByCategoryId(category_id: String): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("product")
                .whereIn("category_id",listOf(category_id))
                .orderBy("product_count", Query.Direction.DESCENDING)
                .limit(8)
                .get()
        }

    // (FIRESTORE) GET list of next 8 products
    fun getNextProductsByCategoryId(lastVisiblePostCount: Int, category_id: String): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("product")
                .whereIn("category_id",listOf(category_id))
                .orderBy("product_count", Query.Direction.DESCENDING)
                .startAfter(lastVisiblePostCount)
                .limit(8)
                .get()
        }

    // (FIRESTORE) GET list of first 8 permintaan
    fun getFirstPermintaanWithNoFilter(): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_count", Query.Direction.DESCENDING
            ).limit(8).get()
        }

    // (FIRESTORE) GET list of next 8 permintaan
    fun getNextPermintaanWithNoFilter(lastVisiblePermintaanCount: Int): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb.collection("permintaan").orderBy(
                "permintaan_count", Query.Direction.DESCENDING
            ).startAfter(lastVisiblePermintaanCount).limit(8).get()
        }

    // (FIRESTORE) GET list of first 8 permintaan by category
    fun getFirstPermintaanByCategoryId(category_id: String): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("permintaan")
                .whereIn("category_id",listOf(category_id))
                .orderBy("permintaan_count", Query.Direction.DESCENDING)
                .limit(8)
                .get()
        }

    // (FIRESTORE) GET list of next 8 permintaan by category
    fun getNextPermintaanByCategoryId(lastVisiblePermintaanCount: Int, category_id:String): Flow<Resource<List<PermintaanModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("permintaan")
                .whereIn("category_id",listOf(category_id))
                .orderBy("permintaan_count", Query.Direction.DESCENDING)
                .startAfter(lastVisiblePermintaanCount)
                .limit(8)
                .get()
        }

    // (FIRESTORE) GET list of MY first 8 product
    fun getMyFirstProducts(): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("product")
                .whereIn("seller_id", listOf(getCurrentUid()))
                .orderBy("product_count", Query.Direction.DESCENDING)
                .limit(8)
                .get()
        }

    // (FIRESTORE) GET list of MY next 8 products
    fun getMyNextProducts(lastVisibleCount: Int): Flow<Resource<List<ProductModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("product")
                .whereIn("seller_id", listOf(getCurrentUid()))
                .orderBy("product_count", Query.Direction.DESCENDING)
                .startAfter(lastVisibleCount)
                .limit(8)
                .get()
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

    // (FIRESTORE) GET business by uid
    fun getBusinessByUid(): Flow<Resource<List<BusinessModel>>?> =
        getResponse.getFirestoreListResponse {
            firestoreDb
                .collection("business")
                .whereGreaterThanOrEqualTo("uid", getCurrentUid())
                .whereLessThanOrEqualTo("uid", "${getCurrentUid()}\uF7FF")
                .get()
        }

    // (FIRESTORE) GET chat data by chat_id
    fun getChatDataByChatId(chat_id: String): Flow<Resource<ChatDataModel>?> =
        getResponse.getFirestoreResponse(timeDelay = 0L) {
            firestoreDb
                .collection("chat_data")
                .document(chat_id)
                .get()
        }

    // (REALTIME DB) SEND message
    fun sendMessage(
        channel_id: String,
        chat: String,
        sender: String,
        receiver: String,
        product_id: String,
        permintaan_id: String,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDb
            .reference
            .child("chat")
            .child(channel_id)
            .child("chat_data")
            .child("count")
            .get()
            .addOnSuccessListener {
                val count = it.value as Long
                val chatRoomRef = realtimeDb.reference
                    .child("chat")
                    .child(channel_id)
                    .child("chat_room")
                val randomId = chatRoomRef.push().key
                val chat_id = "$count-$randomId"
                val sendChatRequest = ChatItemModel(
                    chat_id = chat_id,
                    channel_id = channel_id,
                    sender = sender,
                    receiver = receiver,
                    count = count.toInt(),
                    chat = chat,
                    product_id = product_id,
                    permintaan_id = permintaan_id
                )

                chatRoomRef
                    .child(chat_id)
                    .setValue(sendChatRequest)
                    .addOnSuccessListener {
                        realtimeDb
                            .reference
                            .child("chat")
                            .child(channel_id)
                            .child("chat_data")
                            .child("count")
                            .setValue(count + 1)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onFailed()
                            }
                    }
                    .addOnFailureListener {
                        onFailed()
                    }
            }
            .addOnFailureListener {
                onFailed()
            }
    }

    // (REALTIME DB) LISTEN to chat by channel_id
    fun listenChatByChannelId(
        channel_id: String,
        onDataChange: (DataSnapshot) -> Unit,
        onCancelled: () -> Unit
    ) {
        realtimeDb
            .reference
            .child("chat")
            .child(channel_id)
            .child("chat_room")
            .orderByChild("count")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onDataChange(snapshot)
                }

                override fun onCancelled(error: DatabaseError) {
                    onCancelled()
                }
            })
    }

    // (REALTIME DB) CREATE new chat channel
    fun createNewChatChannel(
        channel_id: String,
        user_1: String,
        user_2: String,
        onSuccess: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDb
            .reference
            .child("chat")
            .child(channel_id)
            .child("chat_data")
            .setValue(
                ChatDataModel(
                    channel_id = channel_id,
                    count = 0,
                    uid_1 = user_1,
                    uid_2 = user_2,
                    last_chat = ""
                )
            )
            .addOnSuccessListener {
                firestoreDb
                    .collection("chat_room")
                    .document(channel_id)
                    .set(
                        ChatDataModel(
                            channel_id = channel_id,
                            count = 0,
                            uid_1 = user_1,
                            uid_2 = user_2,
                            last_chat = ""
                        )
                    )
                    .addOnSuccessListener { onSuccess(channel_id) }
                    .addOnFailureListener { onFailed() }
            }
            .addOnFailureListener { onFailed() }
    }

    // (REALTIME DB) GET available chat channel
    fun getAvailableChatChannel(
        possibleChannel1: String,
        possibleChannel2: String,
        onSuccess: (shouldCreateNew: Boolean, channel: String) -> Unit,
        onFailed: () -> Unit
    ) {
        realtimeDb
            .reference
            .child("chat")
            .get()
            .addOnSuccessListener {
                when {
                    it.child(possibleChannel1).exists() -> {
                        onSuccess(false, possibleChannel1)
                        return@addOnSuccessListener
                    }

                    it.child(possibleChannel2).exists() -> {
                        onSuccess(false, possibleChannel2)
                        return@addOnSuccessListener
                    }

                    else -> {
                        onSuccess(true, possibleChannel1)
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener {
                onFailed()
            }
    }

    // (FIRESTORE) Update last chat on chatdata
    fun updateLastChatOnFirestore(
        channel_id: String,
        last_chat: String,
        onFailed: () -> Unit,
        onSuccess: () -> Unit
    ) {
        firestoreDb
            .collection("chat_room")
            .document(channel_id)
            .get()
            .addOnSuccessListener {
                val res = it.toObject(ChatDataModel::class.java)

                res?.let {
                    firestoreDb
                        .collection("chat_room")
                        .document(channel_id)
                        .set(it.copy(last_chat = last_chat))
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailed() }
                }
            }.addOnFailureListener {
                onFailed()
            }
    }

    // (FIRESTORE) GET list of chat room uid1 from firestore
    fun getListOfChatRoomUid1(uid: String): Flow<Resource<List<ChatDataModel>>?> =
        getResponse.getFirestoreListResponse(timeDelay = 0L) {
            firestoreDb
                .collection("chat_room")
                .whereGreaterThanOrEqualTo("uid_1", uid)
                .whereLessThanOrEqualTo("uid_1", "$uid\uF7FF")
                .get()
        }

    // (FIRESTORE) GET list of chat room uid2 from firestore
    fun getListOfChatRoomUid2(uid: String): Flow<Resource<List<ChatDataModel>>?> =
        getResponse.getFirestoreListResponse(timeDelay = 0L) {
            firestoreDb
                .collection("chat_room")
                .whereGreaterThanOrEqualTo("uid_2", uid)
                .whereLessThanOrEqualTo("uid_2", "$uid\uF7FF")
                .get()
        }

    // (STORAGE & FIRESTORE) SAVE images to storage and then to firestore
    fun saveProductImagesToStorageAndFirestore(
        product_id: String,
        listUri: List<Uri>,
        successCountState: MutableState<Int>,
        thumbnailUrlState: MutableState<String>,
        onSuccess: () -> Unit,
        onFailed: () -> Unit
    ) {
        listUri.forEachIndexed { index, uri ->
            storage
                .reference
                .child("product_images/${index}-${product_id}.png")
                .putFile(uri)
                .addOnSuccessListener {
                    it.storage
                        .downloadUrl
                        .addOnSuccessListener {
                            if (index == 0) {
                                thumbnailUrlState.value = it.toString()
                            }

                            val body = ProductPictureModel(
                                picture_id = "${index}-${product_id}",
                                product_id = product_id,
                                picture_url = it.toString()
                            )

                            firestoreDb
                                .collection("product_images")
                                .document("${index}-${product_id}")
                                .set(body)
                                .addOnSuccessListener {
                                    successCountState.value += 1

                                    if (successCountState.value == listUri.size) {
                                        onSuccess()
                                    }
                                }
                                .addOnFailureListener {
                                    onFailed()
                                    return@addOnFailureListener
                                }
                        }
                        .addOnFailureListener {
                            onFailed()
                            return@addOnFailureListener
                        }
                }
                .addOnFailureListener {
                    onFailed()
                    return@addOnFailureListener
                }
        }
    }

    // (FIRESTORE) GET last count for product
    fun getLastCountProduct(
        onSuccess: (Int) -> Unit,
        onFailed: () -> Unit
    ) {
        val productRef = firestoreDb.collection("product")

        productRef
            .get()
            .addOnSuccessListener {
                onSuccess(it.size())
            }.addOnFailureListener {
                onFailed()
            }
    }

    // (FIRESTORE) SAVE product detail
    fun saveProductDetail(
        onSuccess: (ProductModel) -> Unit,
        onFailed: () -> Unit,
        product_id: String,
        product_count: Int,
        product_name: String,
        product_description: String,
        product_price: String,
        product_thumbnail: String,
        product_quantity: Int,
        product_unit: String,
        category_id: String
    ) {
        val body = ProductModel(
            product_id = product_id,
            product_count = product_count,
            product_name = product_name,
            product_description = product_description,
            product_price = product_price,
            product_thumbnail = product_thumbnail,
            product_quantity = product_quantity,
            product_unit = product_unit,
            category_id = category_id,
            seller_id = getCurrentUid()
        )

        firestoreDb
            .collection("product")
            .document(product_id)
            .set(body)
            .addOnSuccessListener { onSuccess(body) }
            .addOnFailureListener { onFailed() }
    }

    // (FIRESTORE) SAVE fcm token to firestore
    fun saveFcmTokenToFirestore(token: String) {
        firestoreDb
            .collection("fcm_token")
            .document(getCurrentUid())
            .set(
                FcmTokenModel(
                    uid = getCurrentUid(),
                    token = token
                )
            )
    }

    // (FIRESTORE) GET fcm token by uid
    fun getFcmTokenByUid(uid: String): Flow<Resource<FcmTokenModel>?> =
        getResponse.getFirestoreResponse(timeDelay = 0L) {
            firestoreDb
                .collection("fcm_token")
                .document(uid)
                .get()
        }

    // (MESSAGING) SUBSCRIBE to target_uid
    fun subscribeChatToTargetUid(target_uid: String) {
        messaging.subscribeToTopic(target_uid)
    }

    // (MESSAGING) SEND notification
    suspend fun sendCloudNotification(
        my_name: String,
        my_message: String,
        target_token: String
    ):Any? = httpClient.post {
        val req = NotificationModel(
            to = target_token,
            data = Notification(
                body = my_message,
                title = my_name,
                tag = getCurrentUid()
            )
        )

        url(FcmRoutes.fcmUrl)
        contentType(ContentType.Application.Json)
        header("Authorization", "Bearer ${FcmRoutes.fcmServerKey}")
        body = req
    }

    // (FIRESTORE) GET product by product_id
    fun getProductByProductId(product_id: String, delay: Long = 2500L):Flow<Resource<ProductModel>?> =
        getResponse.getFirestoreResponse(timeDelay = delay) {
            firestoreDb.collection("product").document(product_id).get()
        }

    // (FIRESTORE) GET permintaan by permintaan_id
    fun getPermintaanByPermintaanId(permintaan_id: String, delay: Long = 2500L):Flow<Resource<PermintaanModel>?> =
        getResponse.getFirestoreResponse(timeDelay = delay) {
            firestoreDb.collection("permintaan").document(permintaan_id).get()
        }
}
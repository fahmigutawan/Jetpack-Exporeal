package com.bcc.exporeal.di

import android.content.Context
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.util.ConnectivityCheck
import com.bcc.exporeal.util.GetResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase() = FirebaseDatabase.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideCOnnectivityCheck(
        @ApplicationContext context: Context
    ) = ConnectivityCheck(context)

    @Provides
    @Singleton
    fun provideGetResponse(
        connectivityCheck: ConnectivityCheck
    ) = GetResponse(connectivityCheck)

    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context,
        realtimeDb: FirebaseDatabase,
        firestoreDb: FirebaseFirestore,
        storage: FirebaseStorage,
        auth: FirebaseAuth,
        getResponse: GetResponse
    ) = AppRepository(context, realtimeDb, firestoreDb, storage, auth, getResponse)
}
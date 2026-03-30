package com.arturojas32.todoapp.data.di

import com.arturojas32.todoapp.data.network.auth.data.AuthRepository
import com.arturojas32.todoapp.data.network.auth.data.AuthRepositoryImpl
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {


    @Provides
    @Singleton
    fun providesFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun providesAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth = auth)
}

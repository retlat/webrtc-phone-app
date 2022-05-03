package com.example.phoneapp

import android.content.ComponentName
import android.content.Context
import android.telecom.PhoneAccountHandle
import com.example.phoneapp.resource.R
import com.example.phoneapp.service.call.CallConnectionService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providePhoneAccountHandle(@ApplicationContext context: Context): PhoneAccountHandle {
        return PhoneAccountHandle(
            ComponentName(
                context,
                CallConnectionService::class.java
            ),
            context.getString(R.string.app_name)
        )
    }
}

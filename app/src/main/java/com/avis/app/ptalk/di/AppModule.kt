package com.avis.app.ptalk.di

import android.content.Context
import androidx.room.Room
import com.avis.app.ptalk.core.ble.BleClient
import com.avis.app.ptalk.core.ble.impl.PTalkBleClient
import com.avis.app.ptalk.domain.control.BleControlGateway
import com.avis.app.ptalk.domain.control.ControlGateway
import com.avis.app.ptalk.domain.data.local.AppDatabase
import com.avis.app.ptalk.domain.data.local.dao.DeviceDao
import com.avis.app.ptalk.domain.data.local.repo.AuthRepository
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
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "ptalk.db").build()

    @Provides
    fun provideDeviceDao(db: AppDatabase): DeviceDao = db.deviceDao()

    @Provides @Singleton
    fun provideBle(@ApplicationContext ctx: Context): BleClient = PTalkBleClient(ctx)

    @Provides @Singleton
    fun provideGateway(ble: BleClient): ControlGateway =
        BleControlGateway { addr -> ble.connect(addr) }

    @Provides @Singleton
    fun provideAuth(): AuthRepository = AuthRepository()
}
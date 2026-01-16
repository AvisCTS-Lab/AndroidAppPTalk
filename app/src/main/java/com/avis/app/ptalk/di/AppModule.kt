package com.avis.app.ptalk.di

import android.content.Context
import androidx.room.Room
import com.avis.app.ptalk.core.ble.BleClient
import com.avis.app.ptalk.core.ble.impl.PTalkBleClient
import com.avis.app.ptalk.core.config.WSConfig
import com.avis.app.ptalk.core.websocket.DeviceControlWebSocket
import com.avis.app.ptalk.domain.control.BleControlGateway
import com.avis.app.ptalk.domain.control.ControlGateway
import com.avis.app.ptalk.domain.data.local.AppDatabase
import com.avis.app.ptalk.domain.data.local.dao.DeviceDao
import com.avis.app.ptalk.domain.data.local.repo.AuthRepository
import com.avis.app.ptalk.domain.data.local.repo.DeviceRepository
import com.avis.app.ptalk.domain.service.DeviceControlService
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

    @Provides
    @Singleton
    fun provideDeviceRepository(dao: DeviceDao): DeviceRepository = DeviceRepository(dao)

    @Provides
    @Singleton
    fun provideBle(@ApplicationContext ctx: Context): BleClient = PTalkBleClient(ctx)

    @Provides
    @Singleton
    fun provideGateway(ble: BleClient): ControlGateway =
        BleControlGateway { addr -> ble.connect(addr) }

    @Provides
    @Singleton
    fun provideAuth(): AuthRepository = AuthRepository()

    // Server configuration
    @Provides
    @Singleton
    fun provideWSConfig(@ApplicationContext ctx: Context): WSConfig = WSConfig(ctx)

    // WebSocket for real-time control
    @Provides
    @Singleton
    fun provideDeviceControlWebSocket(): DeviceControlWebSocket = DeviceControlWebSocket()

    @Provides
    @Singleton
    fun provideDeviceControlService(
        webSocket: DeviceControlWebSocket,
        serverConfig: WSConfig
    ): DeviceControlService = DeviceControlService(webSocket, serverConfig.getServerUrl())
}
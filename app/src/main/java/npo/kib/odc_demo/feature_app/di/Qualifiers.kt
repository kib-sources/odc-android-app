package npo.kib.odc_demo.feature_app.di

import javax.inject.Qualifier

@Qualifier
annotation class BluetoothP2PConnection

@Qualifier
annotation class BluetoothP2PConnectionBidirectional

@Qualifier
annotation class NfcP2PConnection

@Qualifier
annotation class NfcP2PConnectionBidirectional

@Qualifier
annotation class ReceiveUseCase

@Qualifier
annotation class SendUseCase

@Qualifier
annotation class AtmUseCase
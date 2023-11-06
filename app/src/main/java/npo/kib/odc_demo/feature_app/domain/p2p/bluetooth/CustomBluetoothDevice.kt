package npo.kib.odc_demo.feature_app.domain.p2p.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

data class CustomBluetoothDevice(val name: String?, val address: String)

@SuppressLint("MissingPermission")
fun BluetoothDevice.toCustomBluetoothDevice(): CustomBluetoothDevice =
    CustomBluetoothDevice(name = name, address = address)
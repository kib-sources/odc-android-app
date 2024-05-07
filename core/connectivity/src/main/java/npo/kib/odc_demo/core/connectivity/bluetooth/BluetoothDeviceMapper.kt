package npo.kib.odc_demo.core.connectivity.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import npo.kib.odc_demo.core.model.CustomBluetoothDevice


@SuppressLint("MissingPermission")
fun BluetoothDevice.toCustomBluetoothDevice(): CustomBluetoothDevice =
    CustomBluetoothDevice(name = name, address = address)
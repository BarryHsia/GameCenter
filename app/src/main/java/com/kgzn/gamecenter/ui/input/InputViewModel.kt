package com.kgzn.gamecenter.ui.input

import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.input.InputManager
import android.util.Log
import android.view.InputDevice
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel

class InputViewModel(context: Context) : ViewModel() {

    companion object {
        const val TAG = "InputViewModel"
    }

    val devices = SnapshotStateList<InputDevice>()

    private val mInputManager: InputManager = context.getSystemService(InputManager::class.java)
    private val mBluetoothManager: BluetoothManager = context.getSystemService(BluetoothManager::class.java)

    private val mInputDeviceListener = object : InputManager.InputDeviceListener {
        override fun onInputDeviceAdded(deviceId: Int) {
            Log.d(TAG, "onInputDeviceAdded: $deviceId")
            val device = mInputManager.getInputDevice(deviceId)?.takeIf { it.isGameController() }
            if (device != null) {
                devices.add(device)
            }
        }

        override fun onInputDeviceRemoved(deviceId: Int) {
            Log.d(TAG, "onInputDeviceRemoved: $deviceId")
            devices.removeIf { it.id == deviceId }
        }

        override fun onInputDeviceChanged(deviceId: Int) {
            Log.d(TAG, "onInputDeviceChanged: $deviceId")
            val device = mInputManager.getInputDevice(deviceId)?.takeIf { it.isGameController() }
            if (device == null) {
                devices.removeIf { it.id == deviceId }
            } else {
                val indexOfFirst = devices.indexOfFirst { it.id == deviceId }
                if (indexOfFirst == -1) {
                    devices.add(device)
                } else {
                    devices[indexOfFirst] = device
                }
            }
        }

    }

    init {
        mInputManager.inputDeviceIds.map { mInputManager.getInputDevice(it) }.filterNotNull()
            .filter { it.isGameController() }.filter { it.isExternal }.let { devices.addAll(it) }
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null)
    }

    override fun onCleared() {
        Log.d(TAG, "onCleared: ")
        mInputManager.unregisterInputDeviceListener(mInputDeviceListener)
    }

    fun disconnectDevice(deviceId: Int) {
        val device = mInputManager.getInputDevice(deviceId) ?: return
        val bluetoothAddress = try {
            val method = device.javaClass.getMethod("getBluetoothAddress")
            method.invoke(device) as? String
        } catch (e: Exception) {
            null
        }
        if (bluetoothAddress != null) {
            try {
                val remoteDevice = mBluetoothManager.adapter?.getRemoteDevice(bluetoothAddress)
                remoteDevice?.javaClass?.getMethod("disconnect")?.invoke(remoteDevice)
            } catch (e: Exception) {
                Log.e(TAG, "disconnectDevice failed", e)
            }
        }
    }
}

private fun InputDevice.isGameController(): Boolean {
    return sources and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD
            || sources and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK
}

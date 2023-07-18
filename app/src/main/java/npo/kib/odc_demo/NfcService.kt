package npo.kib.odc_demo

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import npo.kib.odc_demo.core.Crypto.toHex
import npo.kib.odc_demo.data.p2p.nfc.ApduCommands
import npo.kib.odc_demo.data.p2p.nfc.NfcServiceCommands
import npo.kib.odc_demo.data.p2p.nfc.NfcServiceKeys
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class NfcService : HostApduService() {

    private val selectApdu = byteArrayOf(
        0x00,   //CLA
        0xA4.toByte(),  //INS
        0x04,   //P1
        0x00,   //P2
        0x07,   //Length of AID
        0xF1.toByte(), 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,  //AID
        0x00    //Le
    )

    private var isServiceEnabled = false

    private var isReceiving = false
    private val receiveStream = ByteArrayOutputStream()

    private var startSending = false

    //reversed and divided bytearray
    private var sendingBuffer: ArrayList<ByteArray>? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val enable = intent?.extras?.getSerializable(NfcServiceKeys.SERVICE_ENABLE_KEY)
        if (enable != null) {
            isServiceEnabled = when (enable as NfcServiceCommands) {
                NfcServiceCommands.ENABLE -> true
                NfcServiceCommands.DISABLE -> {
                    Log.d("ODC", "service disabled")
                    false
                }
            }
        }

        val sendingData = intent?.extras?.getByteArray(NfcServiceKeys.SEND_KEY)
        if (sendingData != null) {
            sendingBuffer = splitBytes(data = sendingData)
            startSending = true
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {

        if (commandApdu == null) return byteArrayOf(ApduCommands.ERROR)

        if (!isServiceEnabled) return byteArrayOf(ApduCommands.REJECTED)

        myLogs(commandApdu.decodeToString() + " message " + commandApdu.toHex())

        //Инициализация соединения
        if (commandApdu.contentEquals(selectApdu)) {
            val broadcastIntent =
                Intent(NfcServiceKeys.NFC_BROADCAST_FILTER).putExtra(
                    NfcServiceKeys.CONNECTED_KEY,
                    true
                )
            sendBroadcast(broadcastIntent)
            return byteArrayOf(ApduCommands.CONNECTED)
        }

        if (isReceiving) {
            receiveData(commandApdu)
            return byteArrayOf(ApduCommands.RECEIVED)
        }

        if (commandApdu.size == 1) {
            return handleApduCommand(commandApdu[0])
        }
        return byteArrayOf(ApduCommands.WAIT)
    }

    override fun onDeactivated(reason: Int) {
        val broadcastIntent =
            Intent(NfcServiceKeys.NFC_BROADCAST_FILTER).putExtra(
                NfcServiceKeys.CONNECTED_KEY,
                false
            )
        sendBroadcast(broadcastIntent)
        isReceiving = false
        receiveStream.reset()
        startSending = false
        sendingBuffer = null
    }

    /**
     * Split ByteArray into List of shorter ByteArrays to send via NFC
     * @param data ByteArray for splitting
     * @param splitSize length of one segment. Default length is 252 bytes
     * @return reversed and split ArrayList of ByteArray
     */
    private fun splitBytes(data: ByteArray, splitSize: Int = 252): ArrayList<ByteArray> {
        val buffer = ArrayList<ByteArray>(data.size / splitSize + 1)
        var bytes = data
        while (bytes.isNotEmpty()) {
            val part = bytes.takeLast(splitSize).toByteArray()
            buffer.add(part)
            bytes = bytes.dropLast(splitSize).toByteArray()
        }
        return buffer
    }

    private fun receiveData(commandApdu: ByteArray) {
        if (commandApdu.size == 1 && commandApdu[0] == ApduCommands.END_OF_MESSAGE) {
            val broadcastIntent =
                Intent(NfcServiceKeys.NFC_BROADCAST_FILTER).putExtra(
                    NfcServiceKeys.RECEIVED_KEY,
                    receiveStream.toByteArray()
                )
            sendBroadcast(broadcastIntent)
            isReceiving = false
            receiveStream.reset()
        } else {
            receiveStream.write(commandApdu)
        }
    }

    private fun handleApduCommand(command: Byte): ByteArray {
        when (command) {
            ApduCommands.FROM_ATM -> {
                isReceiving = true
                return byteArrayOf(ApduCommands.RECEIVED)
            }
            ApduCommands.REQUEST -> {
                if (startSending) {
                    startSending = false
                    return byteArrayOf(ApduCommands.FROM_CLIENT)
                }
                if (sendingBuffer == null) {
                    return byteArrayOf(ApduCommands.WAIT)
                }
                if (sendingBuffer!!.isEmpty()) {
                    sendingBuffer = null
                    return byteArrayOf(ApduCommands.END_OF_MESSAGE)
                }
                return sendingBuffer!!.removeLast()
            }
            else -> return byteArrayOf(ApduCommands.WAIT)
        }
    }

}
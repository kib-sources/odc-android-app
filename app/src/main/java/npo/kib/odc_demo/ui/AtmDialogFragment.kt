package npo.kib.odc_demo.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class AtmDialogFragment : DialogFragment() {

    private var onCloseCallback: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Банкомат")
                .setMessage("Для обмена бумажной наличности на цифровую, вставте купюры в купюроприемник банкомата.")
                .setPositiveButton("Завершить") { dialog, id ->
                    onCloseCallback?.invoke()
                    dialog.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun addOnCloseHandler(block: () -> Unit) {
        onCloseCallback = block
    }
}
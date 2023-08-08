package npo.kib.odc_demo.common.util

import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

fun myLogs(msg: Any?) = Log.d("myLogs", msg.toString())

fun Fragment.makeToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Toast.makeText(requireActivity(), text, duration).show()
}


//inline fun <T> LifecycleOwner.collectFlow(flow: Flow<T>, crossinline block: suspend (T) -> Unit) {
//    lifecycleScope.launch {
//        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            flow.collect(block)
//        }
//    }
//}

fun <T> LifecycleOwner.collectFlow(flow: Flow<T>, block: suspend (T) -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(FlowCollector(block))
        }
    }
}
package npo.kib.odc_demo.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import npo.kib.odc_demo.R
import npo.kib.odc_demo.SwitcherInterface
import npo.kib.odc_demo.makeToast
import npo.kib.odc_demo.data.models.AmountRequest
import npo.kib.odc_demo.data.models.ConnectingStatus
import npo.kib.odc_demo.data.models.SearchingStatus
import npo.kib.odc_demo.databinding.SendFragmentBinding

class SendFragment : Fragment() {

    companion object {
        fun newInstance() = SendFragment()
    }

    private var _binding: SendFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    private lateinit var viewModel: SendViewModel
    private lateinit var mController: SwitcherInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (context is SwitcherInterface) {
            mController = context as SwitcherInterface
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement SwitcherInterface"
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SendFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SendViewModel::class.java]
        viewModel.getSum()
        var currentAmount = 0

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.amountRequestFlow.collect { request: AmountRequest? ->
                    if (request != null) {
                        showRequestDialog(request.amount, request.userName, request.wid)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sum.collect {
                    currentAmount = it ?: 0
                    binding.textViewBalanceSend.text =
                        String.format(getString(R.string.your_balance_rubles), currentAmount)
                }
            }
        }

        binding.buttonSend.setOnClickListener {
            val amountString = binding.editTextSend.text.toString()
            if (amountString.isNotEmpty()) {
                val amountToSend = amountString.toInt()
                if (amountToSend > 0) {
                    if (amountToSend <= currentAmount) {
                        viewModel.sendBanknotes(amountToSend)
                    } else makeToast(getString(R.string.insufficient_funds))
                }
            } else makeToast(getString(R.string.enter_sum))

        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchingStatusFlow.collect {
                    when (it) {
                        SearchingStatus.NONE -> viewModel.startAdvertising()
                        SearchingStatus.FAILURE -> binding.sendingStatusView.text =
                            getString(R.string.searching_failure)
                        SearchingStatus.DISCOVERING -> binding.sendingStatusView.text =
                            getString(R.string.searching_device)
                        SearchingStatus.ADVERTISING -> binding.sendingStatusView.text =
                            getString(R.string.searching_device)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isSendingFlow.collect { isSending: Boolean? ->
                    if (isSending != null) {
                        if (isSending) {
                            makeVisible(false)
                            binding.sendingStatusView.text = getString(R.string.sending_is_going)
                        } else {
                            makeToast(getString(R.string.sending_completed))
                            mController.openWalletFragment()
                        }
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.connectionResult.collect {
                    when (it) {
                        is ConnectingStatus.ConnectionInitiated -> {
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_initiated)
                            showConnectionDialog(it.info)
                        }
                        is ConnectingStatus.ConnectionResult ->
                            when (it.result.status.statusCode) {
                                ConnectionsStatusCodes.STATUS_OK -> {
                                    binding.sendingStatusView.text =
                                        getString(R.string.connecting_ok)
                                    binding.buttonSend.isEnabled = true
                                }
                                ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED -> {
                                    binding.sendingStatusView.text =
                                        getString(R.string.connecting_rejected)
                                }
                                ConnectionsStatusCodes.STATUS_ERROR -> {
                                    binding.sendingStatusView.text =
                                        getString(R.string.connecting_error)
                                }
                                else -> {
                                    binding.sendingStatusView.text =
                                        getString(R.string.connecting_undefined_error)
                                }
                            }
                        ConnectingStatus.Disconnected -> {
                            viewModel.getSum()
                            makeVisible(true)
                            binding.sendingStatusView.text =
                                getString(R.string.connecting_disconnected)
                            binding.buttonSend.isEnabled = false
                        }
                        ConnectingStatus.NoConnection -> {
                            binding.buttonSend.isEnabled = false
                            binding.sendingStatusView.text = getString(R.string.searching_device)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAdvertising()
    }

    private fun makeVisible(visible: Boolean) {
        if (visible) {
            binding.sendTitle.visibility = View.VISIBLE
            binding.editTextSend.visibility = View.VISIBLE
            binding.buttonSend.visibility = View.VISIBLE
            binding.progressBarSend.visibility = View.INVISIBLE
        } else {
            binding.sendTitle.visibility = View.INVISIBLE
            binding.editTextSend.visibility = View.INVISIBLE
            binding.buttonSend.visibility = View.INVISIBLE
            binding.progressBarSend.visibility = View.VISIBLE
        }
    }

    private fun showConnectionDialog(info: ConnectionInfo) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.accept_connection_to) + info.endpointName)
            .setMessage(getString(R.string.make_sure) + info.authenticationDigits)
            .setPositiveButton(
                getString(R.string.receive)
            ) { _, _ ->  // The user confirmed, so we can accept the connection.
                viewModel.acceptConnection()
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { _, _ ->  // The user canceled, so we should reject the connection.
                viewModel.rejectConnection()
            }
            //           .setIcon(ContextCompat.getDrawable(view.context, R.drawable.ic_dialog_alert))
            .show()
            .setCanceledOnTouchOutside(false)
    }

    private fun showRequestDialog(amount: Int, userName: String, wid: String) {
        val dialogView = layoutInflater.inflate(R.layout.request_dialog, null)
        dialogView.setBackgroundResource(R.drawable.border_radius)

        val descriptionView = dialogView.findViewById<TextView>(R.id.dialogDescription)
        val amountView = dialogView.findViewById<TextView>(R.id.dialogAmount)
        val sendButton = dialogView.findViewById<Button>(R.id.dialogButtonSend)
        val denyButton = dialogView.findViewById<Button>(R.id.dialogButtonDeny)

        descriptionView.text =
            String.format(getString(R.string.request_banknotes_description), userName, wid)
        amountView.text = String.format(getString(R.string.balance_rubles), amount)

        val alert = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        sendButton.setOnClickListener {
            viewModel.sendBanknotes(amount)
            alert.cancel()
        }
        denyButton.setOnClickListener {
            viewModel.sendRejection()
            alert.cancel()
        }
        alert.show()
    }
}
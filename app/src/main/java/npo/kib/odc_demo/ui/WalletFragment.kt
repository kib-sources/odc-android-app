package npo.kib.odc_demo.ui

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import npo.kib.odc_demo.R
import npo.kib.odc_demo.data.models.ServerConnectionStatus
import npo.kib.odc_demo.SwitcherInterface
import npo.kib.odc_demo.makeToast
import npo.kib.odc_demo.databinding.WalletFragmentBinding

class WalletFragment : Fragment() {

    companion object {
        fun newInstance() = WalletFragment()
    }

    private var _binding: WalletFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: WalletViewModel
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
        _binding = WalletFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[WalletViewModel::class.java]
        viewModel.getSum()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sum.collect {
                    val amount = it ?: 0
                    binding.walletAmount.text =
                        String.format(getString(R.string.balance_rubles), amount)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.serverConnectionStatus.collect { serverConnectionStatus: ServerConnectionStatus ->
                    when (serverConnectionStatus) {
                        ServerConnectionStatus.LOADING -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        ServerConnectionStatus.ERROR -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            makeToast(getString(R.string.connection_error))
                            viewModel.getSum()
                        }
                        ServerConnectionStatus.WALLET_ERROR -> makeToast(getString(R.string.wallet_not_registered))
                        ServerConnectionStatus.SUCCESS -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            viewModel.getSum()
                        }
                    }
                }
            }
        }

        binding.buttonIssueBanknotes.setOnClickListener {
            val amountString = binding.issueEditText.text.toString()
            if (amountString.isNotEmpty()) {
                val amount = amountString.toInt()
                if (amount > 0) viewModel.issueBanknotes(amount)
            } else makeToast(getString(R.string.enter_sum))
        }

        binding.buttonRequireBanknotes.setOnClickListener {
            if (viewModel.isWalletRegistered()) mController.openRequireFragment()
            else makeToast(getString(R.string.wallet_not_registered))
        }

        binding.sendButton.setOnClickListener {
            if (viewModel.isWalletRegistered()) mController.openSendFragment()
            else makeToast(getString(R.string.wallet_not_registered))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
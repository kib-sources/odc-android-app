package npo.kib.odc_demo.feature_app.presentation.top_level_screens.home_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import npo.kib.odc_demo.R
import npo.kib.odc_demo.feature_app.presentation.common.SwitcherInterface
import npo.kib.odc_demo.common.util.collectFlow
import npo.kib.odc_demo.feature_app.domain.model.connection_status.ServerConnectionStatus
import npo.kib.odc_demo.databinding.WalletFragmentBinding
import npo.kib.odc_demo.common.util.makeToast
import javax.inject.Inject

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private var _binding: WalletFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    @Inject
    lateinit var viewModel: HomeViewModel

    private lateinit var mController: SwitcherInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (context !is SwitcherInterface) {
            throw RuntimeException("${context.toString()} must implement SwitcherInterface")
        }

        mController = context as SwitcherInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = WalletFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.updateSum()

        viewLifecycleOwner.collectFlow(viewModel.sum) {
            val amount = it ?: 0
            binding.walletAmount.text = String.format(getString(R.string.balance_rubles), amount)
        }

        viewLifecycleOwner.collectFlow(viewModel.serverConnectionStatus) { serverConnectionStatus ->
            when (serverConnectionStatus) {
                ServerConnectionStatus.LOADING -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                ServerConnectionStatus.ERROR -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    makeToast(getString(R.string.connection_error))
                    viewModel.updateSum()
                }

                ServerConnectionStatus.WALLET_ERROR -> makeToast(getString(R.string.wallet_not_registered))
                ServerConnectionStatus.SUCCESS -> {
                    binding.progressBar.visibility = View.INVISIBLE
                    viewModel.updateSum()
                }
            }
        }

        binding.buttonIssueBanknotes.setOnClickListener {
            val amount = binding.issueEditText.text.toString().toIntOrNull()
            if (amount == null) {
                makeToast(getString(R.string.enter_sum))
                return@setOnClickListener
            }

            if (amount > 0) viewModel.issueBanknotes(amount)
        }

//        binding.buttonGetFromATM.setOnClickListener {
//            viewModel.getBanknotesFromAtmByTcp()
//        }

        //todo uncomment
        /*binding.buttonGetByNfc.setOnClickListener {
            viewModel.getBanknotesFromAtmByNfc()
        }*/

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
package eu.ase.grupa1088.licenta.ui.profile

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivitySchimbaParolaBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.utils.inputValidator
import eu.ase.grupa1088.licenta.utils.value
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.launch

class SchimbaParolaActivity : BaseActivity() {
    override val binding by viewBinding { ActivitySchimbaParolaBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun setupListeners() {
        with(binding) {
            btnResetareParola.setOnClickListener { resetParola() }
        }
    }

    override fun initViews() {}

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.resetPasswordStateFlow.collect {
                handleResponse(it) {
                    displayInfo(getString(R.string.msg_password_reset_info))
                }
            }
        }
    }

    private fun resetParola() {
        with(binding) {
            inputValidator(arrayOf(etEmail to getString(R.string.error_email_required))).also { isValid ->
                if (isValid) {
                    viewModel.resetPassword(etEmail.value())
                }
            }
        }
    }
}
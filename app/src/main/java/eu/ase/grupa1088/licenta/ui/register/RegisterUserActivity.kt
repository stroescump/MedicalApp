package eu.ase.grupa1088.licenta.ui.register

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityRegisterUserBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.login.LoginActivity
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.inputValidator
import eu.ase.grupa1088.licenta.utils.value
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.flow.collectLatest

class RegisterUserActivity : BaseActivity() {
    override val binding by viewBinding(ActivityRegisterUserBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun setupListeners() {
        with(binding) {
            btnRegister.setOnClickListener { registerUser() }
            tvScreenTitle.setOnClickListener { navigateTo(LoginActivity::class.java) }
        }
    }

    override fun initViews() {}

    override fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiStateFlow.collectLatest { res ->
                when (res) {
                    is AppResult.Error -> displayError(res.exception.localizedMessage)
                    AppResult.Progress -> showProgress()
                    is AppResult.Success -> {
                        hideProgress()
                        displayInfo(
                            getString(
                                R.string.msg_registration_successful,
                                res.successData?.nume
                            )
                        )
                    }
                    else -> {}
                }
            }
        }
    }

    private fun registerUser() {
        with(binding) {
            val isInputValid = inputValidator(
                arrayOf(
                    etNume to getString(R.string.error_name_required),
                    etTelefon to getString(R.string.error_phone_required),
                    etEmail to getString(R.string.error_email_required),
                    etParola to getString(R.string.error_password_required),
                    etCNP to getString(R.string.error_CNP_required)
                )
            )
            if (isInputValid) {
                viewModel.registerUser(
                    etEmail.value(),
                    etParola.value(),
                    etNume.value(),
                    etCNP.value(),
                    etTelefon.value()
                )
            } else {
                displayError(getString(R.string.check_data_validity))
            }
        }
    }
}
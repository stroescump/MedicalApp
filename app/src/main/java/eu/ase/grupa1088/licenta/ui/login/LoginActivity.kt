package eu.ase.grupa1088.licenta.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.ProfileActivity
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.SchimbaParolaActivity
import eu.ase.grupa1088.licenta.databinding.ActivityMainBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.ui.register.RegisterUserActivity
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.inputValidator
import eu.ase.grupa1088.licenta.utils.value
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : BaseActivity() {
    override val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            ), Dispatchers.IO
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        with(binding) {
            tvRegister.setOnClickListener { navigateTo(RegisterUserActivity::class.java) }
            btnSignIn.setOnClickListener { userLogin() }
            tvForgotPassword.setOnClickListener { navigateTo(SchimbaParolaActivity::class.java) }
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
                        navigateTo(ProfileActivity::class.java)
                    }
                    null -> {}
                }
            }
        }
    }

    private fun userLogin() {
        with(binding) {
            val isInputValid = inputValidator(
                arrayOf(
                    etEmail to getString(R.string.error_email_required),
                    etPassword to getString(R.string.error_password_required),
                )
            )
            if (isInputValid) {
                viewModel.loginUser(etEmail.value(), etPassword.value())
            } else {
                displayError(getString(R.string.check_data_validity))
            }
        }
    }
}
package eu.ase.grupa1088.licenta.ui.login

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.ui.profile.SchimbaParolaActivity
import eu.ase.grupa1088.licenta.databinding.ActivityMainBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.dashboard.ProfileActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.ui.register.RegisterUserActivity
import eu.ase.grupa1088.licenta.utils.*
import kotlinx.coroutines.flow.collectLatest

class LoginActivity : BaseActivity() {
    override val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        binding.etPassword.text = "123456".toEditable()
        binding.etEmail.text = "stroescump@gmail.com".toEditable()
        binding.btnSignIn.performClick()
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
                        res.successData?.let { user ->
                            navigateTo(ProfileActivity::class.java, true, extras = Bundle().also {
                                it.putParcelable(
                                    USER_KEY, user
                                )
                            })
                        }
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
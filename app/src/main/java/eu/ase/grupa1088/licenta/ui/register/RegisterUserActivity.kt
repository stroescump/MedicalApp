package eu.ase.grupa1088.licenta.ui.register

import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatEditText
import androidx.lifecycle.lifecycleScope
import eu.ase.grupa1088.licenta.LoginActivity
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityRegisterUserBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.value
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.flow.collectLatest

class RegisterUserActivity : BaseActivity() {
    private val viewModel by viewModels<AccountViewModel>()
    override val binding by viewBinding(ActivityRegisterUserBinding::inflate)

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
                        Toast.makeText(
                            this@RegisterUserActivity,
                            "User registered! ${res.successData?.nume}",
                            Toast.LENGTH_SHORT
                        ).show();
                    }
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
//            //pt validarea emailului daca e valid
//            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
//                etEmail.error = "Introduceti un email valid!"
//                etEmail.requestFocus()
//            }
        }
    }

    private fun inputValidator(viewArray: Array<Pair<AppCompatEditText, String>>): Boolean {
        viewArray.onEach {
            if (it.first.value().trim().isEmpty()) {
                it.first.error = it.second
                it.first.requestFocus()
            }
            if (it.first.id == R.id.etParola && it.first.value().length < 6) {
                it.first.error = getString(R.string.error_password_length)
                it.first.requestFocus()
            }
            return false
        }
        return true
    }
}
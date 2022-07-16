package eu.ase.grupa1088.licenta.ui.register

import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityRegisterUserBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.login.LoginActivity
import eu.ase.grupa1088.licenta.utils.*
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
            cbIsDoctor.setOnClickListener {
                cbIsDoctor.isChecked = !cbIsDoctor.isChecked
                handleIsDoctorChecked(cbIsDoctor.isChecked)
            }
        }
    }

    override fun initViews() {
        with(binding) {
            spSpeciality.adapter = initArrayAdapter(
                this@RegisterUserActivity,
                resources.getStringArray(R.array.doctor_specialities)
            )
            cbIsDoctor.isChecked = viewModel.isDoctor
        }
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiStateFlow.collectLatest { res ->
                when (res) {
                    is AppResult.Error -> displayError(res.exception.localizedMessage)
                    AppResult.Progress -> showProgress()
                    is AppResult.Success -> {
                        hideProgress()
                        onBackPressed()
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
                    if (viewModel.isDoctor) etIdDoctor to getString(R.string.error_id_doctor_valid) else etCNP to getString(
                        R.string.error_CNP_required
                    )
                )
            )
            if (isInputValid) {
                viewModel.registerUser(
                    etEmail.value(),
                    etParola.value(),
                    etNume.value(),
                    etTelefon.value(),
                    etCNP.value(),
                    etIdDoctor.value(),
                    spSpeciality.selectedItem?.let { return@let it.toString().uppercase() }
                )
            } else {
                displayError(getString(R.string.check_data_validity))
            }
        }
    }

    private fun ActivityRegisterUserBinding.handleIsDoctorChecked(isDoctor: Boolean) {
        viewModel.isDoctor = isDoctor
        if (isDoctor) {
            etCNP.apply {
                text?.clear()
                hide()
            }
            etIdDoctor.apply {
                text?.clear()
                show()
            }
            spSpeciality.show()
        } else {
            etCNP.apply {
                text?.clear()
                show()
            }
            etIdDoctor.apply {
                text?.clear()
                hide()
            }
            spSpeciality.hide()
        }
    }
}
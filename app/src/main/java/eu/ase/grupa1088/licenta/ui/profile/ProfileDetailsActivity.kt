package eu.ase.grupa1088.licenta.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityProfileDetailsBinding
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.utils.USER_KEY
import eu.ase.grupa1088.licenta.utils.toEditable
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileDetailsActivity : BaseActivity() {
    override val binding by viewBinding(ActivityProfileDetailsBinding::inflate)
    private val user: User by lazy {
        intent.extras?.getParcelable<User>(USER_KEY)
            ?: throw IllegalArgumentException("Must pass a valid user.")
    }
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel.getUserInfo()
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener {
                onBackPressed()
            }
            btnSaveProfile.setOnClickListener {
                viewModel.updateProfile(etPhone.text.toString(), etPassword.text.toString())
            }
        }
    }

    override fun initViews() {}

    override fun setupObservers() {
        viewModel.updateProfile.observe(this) {
            handleResponse(it) {
                lifecycleScope.launch {
                    displayInfo(getString(R.string.info_user_update))
                    withContext(Dispatchers.Main) {
                        if (it) onBackPressed()
                    }
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiStateFlow.collect {
                handleResponse(it) {
                    viewModel.initialPasswd = user.parola.toString()
                    with(binding) {
                        etPhone.text = user.nrTel?.toEditable()
                        etPassword.text = user.parola?.toEditable()
                        tvCNP.text = user.cnp?.toEditable() ?: "Lipsa CNP"
                        tvEmail.text = user.email?.toEditable()
                        tvName.text = user.nume?.toEditable()
                        tvUserType.text = if (user.doctorID.isNullOrBlank()) "Pacient" else "Doctor"
                    }
                }
            }
        }
    }
}
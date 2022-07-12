package eu.ase.grupa1088.licenta.ui.appointments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityAppointmentBinding
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.utils.initArrayAdapter
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.launch

class AppointmentActivity : BaseActivity() {
    private lateinit var user: User
    override val binding by viewBinding(ActivityAppointmentBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
    }

    override fun onStart() {
        super.onStart()
        viewModel.getUserInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAvailableDoctors(getSelectedSpeciality())
    }

    override fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun initViews() {
        with(binding) {
            spinnerDoctor.adapter = initDoctorAdapter(
                this@AppointmentActivity,
                listOf()
            )
            spinnerAppointmentType.adapter =
                initArrayAdapter(
                    this@AppointmentActivity,
                    resources.getStringArray(R.array.doctor_specialities)
                )

            rvAvailableDates.adapter = AppointmentAvailabilityAdapter(
                mutableListOf()
            )
        }
    }

    override fun setupObservers() {
        viewModel.bulkUserLiveData.observe(this) { result ->
            handleResponse(result) { doctorList ->
                if (doctorList.isNotEmpty()) {
                    binding.spinnerDoctor.adapter = initDoctorAdapter(
                        this@AppointmentActivity,
                        doctorList
                    )
                } else {
                    binding.spinnerDoctor.adapter =
                        initDoctorAdapter(this@AppointmentActivity, listOf())
                    getAvailabilityAdapter().refreshAdapter(listOf())
                    displayInfo(getString(R.string.error_no_doctors_available))
                }
            }
        }

        viewModel.medicalAppointmentLiveData.observe(this) { response ->
            handleResponse(response) {
                getAvailabilityAdapter().refreshAdapter(it)
            }
        }

        binding.spinnerAppointmentType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val speciality = getSelectedSpeciality()
                    binding.spinnerDoctor.adapter = initDoctorAdapter(
                        this@AppointmentActivity,
                        listOf()
                    )
                    viewModel.getAvailableDoctors(speciality)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerDoctor.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (this@AppointmentActivity::user.isInitialized) {
                        (binding.spinnerDoctor.selectedItem as User).doctorID?.let { doctorID ->
                            viewModel.getAvailableTimetables(doctorID, "19.07.2022")
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { result ->
                handleResponse(result) {
                    user = it
                }
            }
        }
    }

    private fun getAvailabilityAdapter() =
        (binding.rvAvailableDates.adapter as AppointmentAvailabilityAdapter)


    private fun getSelectedSpeciality() = binding.spinnerAppointmentType.selectedItem as String

    fun initDoctorAdapter(context: Context, list: List<User>) = DoctorsArrayAdapter(
        context,
        R.layout.layout_spinner,
        list
    )
}
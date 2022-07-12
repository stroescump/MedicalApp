package eu.ase.grupa1088.licenta.ui.appointments

import android.R.layout.simple_spinner_dropdown_item
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityAppointmentBinding
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.utils.viewBinding

class AppointmentActivity : BaseActivity() {
    override val binding by viewBinding(ActivityAppointmentBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(
            AccountService(
                FirebaseAuth.getInstance()
            )
        )
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
            spinnerDoctor.adapter = initArrayAdapter(
                arrayOf(
                    "Loading doctors list...",
                )
            )
            spinnerAppointmentType.adapter =
                initArrayAdapter(resources.getStringArray(R.array.doctor_specialities))

            rvAvailableDates.adapter = AppointmentAvailabilityAdapter(
                mutableListOf()
            )
        }
    }

    private fun initArrayAdapter(array: Array<String>) = ArrayAdapter(
        this@AppointmentActivity,
        R.layout.layout_spinner,
        array
    ).also { it.setDropDownViewResource(simple_spinner_dropdown_item) }


    override fun setupObservers() {
        viewModel.bulkUserLiveData.observe(this) { result ->
            handleResponse(result) { doctorList ->
                if (doctorList.isNotEmpty()) {
                    binding.spinnerDoctor.adapter = initArrayAdapter(
                        doctorList.map {
                            it.nume ?: throw IllegalArgumentException("Must have a valid name.")
                        }.toTypedArray()
                    )
                } else {
                    binding.spinnerDoctor.adapter = initArrayAdapter(arrayOf(""))
                    displayInfo(getString(R.string.error_no_doctors_available))
                }
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
                    binding.spinnerDoctor.adapter = initArrayAdapter(
                        arrayOf(
                            "Loading doctors list...",
                        )
                    )
                    viewModel.getAvailableDoctors(speciality)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
    }

    private fun getSelectedSpeciality() = binding.spinnerAppointmentType.selectedItem as String
}
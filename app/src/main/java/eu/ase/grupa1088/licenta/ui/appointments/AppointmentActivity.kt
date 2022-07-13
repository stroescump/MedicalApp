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
import eu.ase.grupa1088.licenta.utils.dateFormat_ddMMyyyy
import eu.ase.grupa1088.licenta.utils.initArrayAdapter
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


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
            btnConfirmAppointment.setOnClickListener {
                spinnerDoctor.selectedItem?.let { doctorDetails ->
                    doctorDetails as User
                    val selectedTime = getAvailabilityAdapter().getList().first { it.isSelected }
                    viewModel.bookAppointment(
                        FirebaseAuth.getInstance().uid!!,
                        selectedTime.copy(
                            doctorID = doctorDetails.doctorID,
                            roomIDConsultation = Random().nextInt(20).toString(),
                            consultationPrice = Random().nextInt(350).toFloat(),
                            name = doctorDetails.nume
                        )
                    )
                }
            }
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

            val calendar = Calendar.getInstance()

            val listOfDates = mutableListOf<String>()
            for (index in 1..10) {
                calendar.add(Calendar.DATE, 1)
                listOfDates.add(dateFormat_ddMMyyyy.format(calendar.time))
            }

            rvAvailableDates.adapter = AppointmentAvailabilityAdapter(mutableListOf())
            rvDateDesired.adapter = DesiredDateAdapter(listOfDates) { dateDesired, _ ->
                binding.spinnerDoctor.selectedItem?.also {
                    (it as User).doctorID?.let { doctorID ->
                        viewModel.getAvailableTimetables(doctorID, dateDesired)
                    }
                }
            }
        }
    }

    override fun setupObservers() {
        viewModel.sendAppointment.observe(this) { result ->
            handleResponse(result) {
                displayInfo(getString(R.string.info_appointment_successful))
                lifecycleScope.launch(Dispatchers.Default) {
                    delay(400)
                    withContext(Dispatchers.Main) {
                        onBackPressed()
                    }
                }
            }
        }

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
            handleResponse(response) { appointmentsRemoteList ->
                val isToday =
                    viewModel.selectedDate == dateFormat_ddMMyyyy.format(Calendar.getInstance().time)
                viewModel.showAvailableDates(isToday, appointmentsRemoteList) { availableDates ->
                    if (availableDates.isEmpty()) {
                        displayError(getString(R.string.error_no_intervals_available))
                    } else {
                        getAvailabilityAdapter().refreshAdapter(
                            availableDates.map { it.copy(date = viewModel.selectedDate) }
                        )
                    }
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
                    getAvailabilityAdapter().refreshAdapter(listOf())
                    val speciality = getSelectedSpeciality()
                    binding.spinnerDoctor.adapter = initDoctorAdapter(
                        this@AppointmentActivity,
                        listOf()
                    )
                    viewModel.getAvailableDoctors(speciality)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        binding.spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getAvailabilityAdapter().refreshAdapter(listOf())
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

    private fun getDatesDesiredAdapter() =
        (binding.rvDateDesired.adapter as DesiredDateAdapter)


    private fun getSelectedSpeciality() = binding.spinnerAppointmentType.selectedItem as String

    fun initDoctorAdapter(context: Context, list: List<User>) = DoctorsArrayAdapter(
        context,
        R.layout.layout_spinner,
        list
    )
}
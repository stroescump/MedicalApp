package eu.ase.grupa1088.licenta.ui.appointments

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityAppointmentBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.utils.dateFormatter
import eu.ase.grupa1088.licenta.utils.initArrayAdapter
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
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
            val dateFormat = SimpleDateFormat("dd.MM.yyyy")
            val listOfDates = mutableListOf<String>()
            for (index in 1..10) {
                calendar.add(Calendar.DATE, 1)
                listOfDates.add(dateFormat.format(calendar.time))
            }

            rvAvailableDates.adapter = AppointmentAvailabilityAdapter(mutableListOf()) {
                displayInfo("${getAvailabilityAdapter().getList()[0].date}")
            }
            rvDateDesired.adapter = DesiredDateAdapter(listOfDates) { dateDesired, _ ->
                binding.spinnerDoctor.selectedItem?.also {
                    (it as User).doctorID?.let { doctorID ->
                        viewModel.getAvailableTimetables(doctorID, dateDesired)
                    }
                }
            }.also { it.setHasStableIds(true) }
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
            handleResponse(response) { appointmentsRemoteList ->
                val calendar = Calendar.getInstance()
                val listOfHours = mutableListOf<MedicalAppointment>()
                var currentTime =
                    LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                val currentMinute = calendar.get(Calendar.MINUTE)
                if (currentMinute > 30) {
                    calendar.add(Calendar.MINUTE, 60 - currentMinute)
                    currentTime = LocalTime.of(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE)
                    )
                } else if (currentMinute in 1..29) {
                    calendar.add(Calendar.MINUTE, 30 - currentMinute)
                    currentTime = LocalTime.of(
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE)
                    )
                }
                val endOfShift = LocalTime.of(17, 30)
                val diff = currentTime.until(endOfShift, ChronoUnit.MINUTES)
                if (diff > 60) {
                    for (i in 1..(diff) step 60) {
                        addHoursToAvailabileSlots(calendar, listOfHours)
                    }
                } else if (diff > 30) {
                    addHoursToAvailabileSlots(calendar, listOfHours)
                }
                appointmentsRemoteList.onEach { appointmentRemote ->
                    listOfHours.removeIf { it.startHour == appointmentRemote.startHour }
                }
                if (listOfHours.isEmpty()) {
                    displayError(getString(R.string.error_no_intervals_available))
                } else {
                    getAvailabilityAdapter().apply {
                        refreshAdapter(
                            listOfHours.map { it.copy(date = viewModel.selectedDate) }
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

        lifecycleScope.launch {
            viewModel.uiStateFlow.collect { result ->
                handleResponse(result) {
                    user = it
                }
            }
        }
    }

    private fun addHoursToAvailabileSlots(
        calendar: Calendar,
        listOfHours: MutableList<MedicalAppointment>
    ) {
        val startHour = dateFormatter.format(calendar.time)
        calendar.add(Calendar.MINUTE, 30)
        val endHour = dateFormatter.format(calendar.time)
        listOfHours.add(
            MedicalAppointment(
                startHour = startHour,
                endHour = endHour
            )
        )
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
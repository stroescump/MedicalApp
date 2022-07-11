package eu.ase.grupa1088.licenta.ui.appointments

import android.R.layout.simple_spinner_dropdown_item
import android.os.Bundle
import android.widget.ArrayAdapter
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityAppointmentBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.viewBinding

class AppointmentActivity : BaseActivity() {
    override val binding by viewBinding(ActivityAppointmentBinding::inflate)

    override fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun initViews() {
        with(binding) {
            spinnerDoctor.adapter = ArrayAdapter(
                this@AppointmentActivity,
                R.layout.layout_spinner,
                arrayOf(
                    "Dr. Radu Bojinca",
                    "Dr. Amelia Radisson",
                    "Dr. Spinaru Eugen",
                    "Dr. Maris Larisa",
                    "Prof. Dr. Coman Laurentiu",
                )
            ).also { it.setDropDownViewResource(simple_spinner_dropdown_item) }

            spinnerAppointmentType.adapter = ArrayAdapter(
                this@AppointmentActivity,
                R.layout.layout_spinner,
                arrayOf(
                    "Fizioterapie",
                    "Fiziokinetoterapie",
                    "Electroterapie",
                    "Hidroterapie",
                    "Termoterapie",
                    "Masaj terapeutic",
                    "Ergoterapia",
                    "Pneumatoterapia",
                    "Radiografie",
                    "Tomografie",
                    "Analize",
                    "Intretinere corporala"
                )
            ).also { it.setDropDownViewResource(simple_spinner_dropdown_item) }

            rvAvailableDates.adapter = AppointmentAvailabilityAdapter(
                mutableListOf(
                    "26.06.2022" to "11:30AM - 12:00PM",
                    "26.06.2022" to "12:30PM - 01:00PM",
                    "26.06.2022" to "01:30PM - 02:00PM",
                    "26.06.2022" to "02:00PM - 02:30PM",
                    "26.06.2022" to "02:30PM - 03:00PM",
                ).map {
                    Triple(it.first, it.second, false)
                }.toMutableList()
            )
        }
    }

    override fun setupObservers() {}
}
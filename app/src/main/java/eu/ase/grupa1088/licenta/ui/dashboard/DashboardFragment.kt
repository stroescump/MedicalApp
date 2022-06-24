package eu.ase.grupa1088.licenta.ui.dashboard

import MedicalAppointmentAdapter
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.ase.grupa1088.licenta.MedicalRecordActivity
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.FragmentDashboardBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.ui.appointments.AppointmentActivity
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.dashboard.DashboardFragment.DashboardItem.*

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            rvCategories.adapter = ItemDashboardAdapter(
                mutableListOf(
                    Programari,
                    TestCovidOnline,
                    Medici,
                    IstoricMedical,
                )
            ) { dashboardItem ->
                when (dashboardItem) {
                    Medici -> TODO()
                    Programari -> navigateTo(AppointmentActivity::class.java)
                    IstoricMedical -> navigateTo(MedicalRecordActivity::class.java)
                    TestCovidOnline -> TODO()
                }
            }
            rvMedicalAppointments.adapter = MedicalAppointmentAdapter(
                mutableListOf(
//                    MedicalAppointment("Dr. Radu Ciobanu", "22.06.2022", "11:30AM", "12:00PM"),
//                    MedicalAppointment("Dr. Andreea Cazacu", "04.07.2022", "10:30AM", "11:00AM"),
//                    MedicalAppointment("Dr. Muresan Ganici", "12.08.2022", "09:30AM", "10:00AM"),
//                    MedicalAppointment("Dr. Prof. Marius Ciorbea", "14.09.2022", "2:30PM", "3:00PM")
                    MedicalAppointment("Caius Marian", "22.06.2022", "11:30AM", "12:00PM"),
                    MedicalAppointment("Rares Dumitru", "04.07.2022", "10:30AM", "11:00AM"),
                    MedicalAppointment("Andreea Costov", "12.08.2022", "09:30AM", "10:00AM"),
                    MedicalAppointment("Danaila Mihai", "14.09.2022", "2:30PM", "3:00PM")
                )
            )
        }
    }

    sealed class DashboardItem(val name: String, val resInt: Int) {
        object Programari : DashboardItem("Programari", R.drawable.appointment)
        object TestCovidOnline : DashboardItem("Test covid online", R.drawable.mask)
        object Medici : DashboardItem("Medici", R.drawable.doctors)
        object IstoricMedical : DashboardItem("Istoric medical", R.drawable.symptoms)
    }
}

fun <T> Fragment.navigateTo(
    destination: Class<T>,
    isFinishActivity: Boolean = false,
    extras: Bundle? = null
) {
    if (destination.superclass.name == BaseActivity::class.java.name) {
        requireActivity().apply {
            Intent(this, destination).also {
                extras?.let { safeExtras -> it.putExtra("data", safeExtras) }
                startActivity(it)
            }
            if (isFinishActivity) finish()
        }
    } else {
        throw IllegalArgumentException("Destination must be an Activity!")
    }
}
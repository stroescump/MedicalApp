package eu.ase.grupa1088.licenta.ui.dashboard

import MedicalAppointmentAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.FragmentDashboardBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment

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
                    "Programari" to R.drawable.mask,
                    "Test covid online" to R.drawable.mask,
                    "Medici" to R.drawable.mask,
                    "Glosar simptome" to R.drawable.mask,
                )
            )
            rvMedicalAppointments.adapter = MedicalAppointmentAdapter(
                mutableListOf(
                    MedicalAppointment("Dr. Radu Ciobanu", "22.06.2022", "11:30AM", "12:00PM"),
                    MedicalAppointment("Dr. Andreea Cazacu", "04.07.2022", "10:30AM", "11:00AM"),
                    MedicalAppointment("Dr. Muresan Ganici", "12.08.2022", "09:30AM", "10:00AM"),
                    MedicalAppointment("Dr. Prof. Marius Ciorbea", "14.09.2022", "2:30PM", "3:00PM")
                )
            )
        }
    }
}
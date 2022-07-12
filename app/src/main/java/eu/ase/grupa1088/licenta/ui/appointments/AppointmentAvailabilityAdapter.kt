package eu.ase.grupa1088.licenta.ui.appointments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemAppointmentDetailsBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.ui.appointments.AppointmentAvailabilityAdapter.AppointmentAvailabilityVH

class AppointmentAvailabilityAdapter(private val appointmentList: MutableList<MedicalAppointment>) :
    RecyclerView.Adapter<AppointmentAvailabilityVH>() {
    private lateinit var binding: ItemAppointmentDetailsBinding

    inner class AppointmentAvailabilityVH : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(appointment: MedicalAppointment, position: Int) {
            with(binding) {
                var isSelected = false
                root.setOnClickListener {
                    //TODO Create logic for selecting state
                }
                if (isSelected) {
                    root.apply {
                        setBackgroundResource(R.drawable.button_bg)
                        tvDateAvailable.setTextColor(
                            resources.getColor(
                                R.color.white, context.theme
                            )
                        )
                        tvHourInterval.setTextColor(
                            resources.getColor(
                                R.color.white,
                                context.theme
                            )
                        )
                    }
                } else {
                    root.apply {
                        setBackgroundResource(R.drawable.card_medical_appointment)
                        tvDateAvailable.setTextColor(
                            resources.getColor(
                                R.color.black, context.theme
                            )
                        )
                        tvHourInterval.setTextColor(
                            resources.getColor(
                                R.color.black,
                                context.theme
                            )
                        )
                    }
                }
                tvDateAvailable.text = appointment.date
                tvHourInterval.text = "${appointment.startHour} - ${appointment.endHour}"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentAvailabilityVH {
        binding = ItemAppointmentDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentAvailabilityVH()
    }

    override fun onBindViewHolder(holder: AppointmentAvailabilityVH, position: Int) {
        val currentDetails = appointmentList[position]
        holder.bind(currentDetails, position)
    }

    override fun getItemCount(): Int = appointmentList.size
    fun refreshAdapter(newList: List<MedicalAppointment>) {
        appointmentList.clear()
        appointmentList.addAll(newList)
        notifyDataSetChanged()
    }
}
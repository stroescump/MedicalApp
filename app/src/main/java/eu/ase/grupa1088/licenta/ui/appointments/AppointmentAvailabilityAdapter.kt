package eu.ase.grupa1088.licenta.ui.appointments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemAppointmentDetailsBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.ui.appointments.AppointmentAvailabilityAdapter.AppointmentAvailabilityVH

class AppointmentAvailabilityAdapter(
    private val appointmentList: MutableList<MedicalAppointment>
) :
    RecyclerView.Adapter<AppointmentAvailabilityVH>() {
    private lateinit var binding: ItemAppointmentDetailsBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentAvailabilityVH {
        binding = ItemAppointmentDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AppointmentAvailabilityVH()
    }

    override fun onBindViewHolder(holder: AppointmentAvailabilityVH, position: Int) {
        holder.setIsRecyclable(false)
        val currentDetails = appointmentList[position]
        holder.bind(currentDetails)
    }

    override fun getItemCount(): Int = appointmentList.size

    inner class AppointmentAvailabilityVH : RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: MedicalAppointment) {
            with(binding) {
                if(appointment.isSelected){
                    root.style(R.style.CustomButton)
                } else {
                    root.style(R.style.CustomButton_Inverted)
                }
                tvDateAvailable.text = appointment.date
                tvHourInterval.text = "${appointment.startHour} - ${appointment.endHour}"
                root.setOnClickListener {
                    appointment.isSelected = !appointment.isSelected
                    if(appointment.isSelected){
                        root.style(R.style.CustomButton)
                    } else {
                        root.style(R.style.CustomButton_Inverted)
                    }
                }
            }
        }
    }

    fun refreshAdapter(newList: List<MedicalAppointment>) {
        appointmentList.clear()
        appointmentList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getList() = appointmentList
}
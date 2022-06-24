package eu.ase.grupa1088.licenta.ui.appointments

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemAppointmentDetailsBinding
import eu.ase.grupa1088.licenta.ui.appointments.AppointmentAvailabilityAdapter.AppointmentAvailabilityVH

class AppointmentAvailabilityAdapter(private val availabilityList: MutableList<Triple<String, String, Boolean>>) :
    RecyclerView.Adapter<AppointmentAvailabilityVH>() {
    private lateinit var binding: ItemAppointmentDetailsBinding

    inner class AppointmentAvailabilityVH : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(availabilityDetails: Triple<String, String, Boolean>, position: Int) {
            with(binding) {
                var isSelected = false
                root.setOnClickListener {
                    availabilityList.onEachIndexed { index, _ ->
                        if (index == position) {
                            isSelected = true
                            availabilityList[index] =
                                Triple(availabilityDetails.first, availabilityDetails.second, true)
                        } else {
                            availabilityList[index] =
                                Triple(availabilityDetails.first, availabilityDetails.second, false)
                        }
                        notifyDataSetChanged()
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
                }
                tvDateAvailable.text = availabilityDetails.first
                tvHourInterval.text = availabilityDetails.second
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
        val currentDetails = availabilityList[position]
        holder.bind(currentDetails, position)
    }

    override fun getItemCount(): Int = availabilityList.size
}
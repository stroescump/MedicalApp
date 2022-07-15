package eu.ase.grupa1088.licenta.ui.appointments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.databinding.ItemAppointmentDesiredDateBinding
import eu.ase.grupa1088.licenta.ui.appointments.DesiredDateAdapter.DesiredDateAdapterVH

data class DateAvailableModel(val date: String, var isSelected: Boolean)

class DesiredDateAdapter(
    private val appointmentDateList: List<String>,
    val onDateClicked: (String, Int) -> Unit
) :
    RecyclerView.Adapter<DesiredDateAdapterVH>() {
    private lateinit var binding: ItemAppointmentDesiredDateBinding

    inner class DesiredDateAdapterVH : RecyclerView.ViewHolder(binding.root) {
        fun bind(dateAvailableModel: String, position: Int) {
            with(binding) {
                root.setOnClickListener {
                    onDateClicked(dateAvailableModel, position)
                }
                root.text = dateAvailableModel
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DesiredDateAdapterVH {
        binding = ItemAppointmentDesiredDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DesiredDateAdapterVH()
    }

    override fun onBindViewHolder(holder: DesiredDateAdapterVH, position: Int) {
        holder.setIsRecyclable(false)
        val currentDetails = appointmentDateList[position]
        holder.bind(currentDetails, position)
    }

    override fun getItemId(position: Int): Long {
        return appointmentDateList[position].hashCode().toLong()
    }

    override fun getItemCount(): Int = appointmentDateList.size
}
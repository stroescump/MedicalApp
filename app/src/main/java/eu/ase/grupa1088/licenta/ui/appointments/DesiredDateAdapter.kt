package eu.ase.grupa1088.licenta.ui.appointments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.style
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemAppointmentDesiredDateBinding
import eu.ase.grupa1088.licenta.ui.appointments.DesiredDateAdapter.DesiredDateAdapterVH

class DesiredDateAdapter(
    private val appointmentDateList: MutableList<String>,
    val onDateClicked: (date: String) -> Unit
) :
    RecyclerView.Adapter<DesiredDateAdapterVH>() {
    private lateinit var binding: ItemAppointmentDesiredDateBinding

    inner class DesiredDateAdapterVH : RecyclerView.ViewHolder(binding.root) {
        fun bind(dateDesired: String) {
            with(binding) {
                root.setOnClickListener {
                    it.isSelected = !it.isSelected
                    if (root.isSelected) {
                        root.style(R.style.CustomButton)
                    } else root.style(R.style.CustomButton_Inverted)
                    onDateClicked(dateDesired)
                }
                root.text = dateDesired
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
        val currentDetails = appointmentDateList[position]
        holder.bind(currentDetails)
    }

    override fun getItemCount(): Int = appointmentDateList.size
    fun refreshAdapter(newList: List<String>) {
        appointmentDateList.clear()
        appointmentDateList.addAll(newList)
        notifyDataSetChanged()
    }


//    private fun isSelected(layoutPos: Int) = selectedPos == layoutPos

}
import MedicalAppointmentAdapter.MedicalAppointmentVH
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemMedicalAppointmentBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment

class MedicalAppointmentAdapter(private val medicalAppointments: MutableList<MedicalAppointment>) :
    RecyclerView.Adapter<MedicalAppointmentVH>() {
    private lateinit var binding: ItemMedicalAppointmentBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalAppointmentVH {
        binding = ItemMedicalAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MedicalAppointmentVH()
    }

    override fun onBindViewHolder(holder: MedicalAppointmentVH, position: Int) {
        val currentItem = medicalAppointments[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = medicalAppointments.size

    inner class MedicalAppointmentVH :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: MedicalAppointment) {
            with(binding) {
                tvAppointmentTitle.text = root.context.getString(R.string.appointment_title_as_doctor)
                tvAppointmentDate.text =
                    "${currentItem.date}\n${currentItem.startingHour} - ${currentItem.endHour}"
                tvAppointmentDoctorName.text = currentItem.doctorName
            }
        }
    }
}
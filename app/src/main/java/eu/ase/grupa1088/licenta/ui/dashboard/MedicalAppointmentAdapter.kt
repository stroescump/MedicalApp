import MedicalAppointmentAdapter.MedicalAppointmentVH
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemMedicalAppointmentBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment

class MedicalAppointmentAdapter(
    private val medicalAppointments: MutableList<MedicalAppointment>,
    private val isDoctor: Boolean,
    private val onAppointmentClicked: (MedicalAppointment, Int, Boolean) -> Unit,
    private val onAppointmentMoreDetails: (MedicalAppointment) -> Unit
) :
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
        holder.setIsRecyclable(false)
        val currentItem = medicalAppointments[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = medicalAppointments.size

    inner class MedicalAppointmentVH :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(currentItem: MedicalAppointment) {
            with(binding) {
                root.setOnClickListener { onAppointmentMoreDetails(currentItem) }
                btnCancelAppointment.setOnClickListener {
                    onAppointmentClicked(
                        currentItem,
                        absoluteAdapterPosition,
                        true
                    )
                }
                btnRescheduleAppointment.setOnClickListener {
                    onAppointmentClicked(
                        currentItem,
                        absoluteAdapterPosition,
                        false
                    )
                }
                tvAppointmentTitle.text = if (isDoctor)
                    getString(R.string.appointment_title_as_doctor) else getString(R.string.appointment_title_as_patient)
                tvAppointmentDate.text =
                    "${currentItem.date}\n${currentItem.startHour} - ${currentItem.endHour}"
                tvAppointmentDoctorName.text =
                    (if (isPatient()) "Dr." else "") + "${currentItem.name}"
            }
        }

        private fun getString(stringRes: Int) =
            binding.root.context.getString(stringRes)
    }

    private fun isPatient() = isDoctor.not()

    fun refreshList(newList: List<MedicalAppointment>) {
        medicalAppointments.clear()
        medicalAppointments.addAll(newList)
        notifyDataSetChanged()
    }

    fun addAppointment(newAppointment: MedicalAppointment?) {
        newAppointment?.let {
            medicalAppointments.add(it)
            notifyItemInserted(medicalAppointments.lastIndex)
        }
    }

    fun removeItem(pos: Int) {
        if (medicalAppointments.isNotEmpty()) {
            medicalAppointments.removeAt(pos)
            notifyItemRemoved(pos)
        }
    }

    fun clear() {
        medicalAppointments.clear()
        notifyDataSetChanged()
    }
}
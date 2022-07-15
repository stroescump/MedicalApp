package eu.ase.grupa1088.licenta.ui.dashboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.FragmentAppointmentDetailsBottomSheetBinding
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.ui.medicalhistory.MedicalRecordViewModel
import eu.ase.grupa1088.licenta.utils.APPOINTMENT_DETAILS
import kotlinx.coroutines.Dispatchers
import kotlinx.parcelize.Parcelize

@Parcelize
data class ParcelablePairAppointment(
    val user: User,
    val medicalAppointment: MedicalAppointment
) : Parcelable

class AppointmentDetailsBottomSheetFragment : BottomSheetDialogFragment() {
    private val appointmentDetails by lazy {
        arguments?.getParcelable<ParcelablePairAppointment>(APPOINTMENT_DETAILS)
            ?: throw IllegalArgumentException("Must have a valid user.")
    }
    private lateinit var parentActivity: ProfileActivity
    private lateinit var binding: FragmentAppointmentDetailsBottomSheetBinding
    private val viewModel: MedicalRecordViewModel by activityViewModels {
        MedicalRecordViewModel.Factory(
            Dispatchers.IO
        )
    }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onStart() {
        super.onStart()
        parentActivity = requireActivity() as ProfileActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            FragmentAppointmentDetailsBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        with(binding) {
            tvAppointmentTitle.text = if (isPatient().not())
                getString(R.string.appointment_title_as_doctor) else getString(R.string.appointment_title_as_patient)
            tvAppointmentDate.text =
                "${appointmentDetails.medicalAppointment.date}\n${appointmentDetails.medicalAppointment.startHour} - ${appointmentDetails.medicalAppointment.endHour}"
            tvAppointmentDoctorName.text =
                (if (isPatient()) "Dr." else "") + "${appointmentDetails.medicalAppointment.name}"
            tvPrice.text =
                "Pretul consultatiei : ${appointmentDetails.medicalAppointment.consultationPrice} Ron"
            tvRoomID.text = "Salonul nr.${appointmentDetails.medicalAppointment.roomIDConsultation}"
        }
    }

    private fun isPatient() = appointmentDetails.user.doctorID.isNullOrBlank()

    companion object {
        @JvmStatic
        fun newInstance(appointment: ParcelablePairAppointment) =
            AppointmentDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply { putParcelable(APPOINTMENT_DETAILS, appointment) }
            }
    }
}
package eu.ase.grupa1088.licenta.ui.medicalhistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.LayoutAddMedicalDataBinding
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.utils.BOTTOM_SHEET_DONE
import eu.ase.grupa1088.licenta.utils.IS_SUCCESS
import eu.ase.grupa1088.licenta.utils.MEDICAL_RECORD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class MedicalData(val sectionName: String) : Parcelable {
    @Parcelize
    object Allergies : MedicalData("Alergii")

    @Parcelize
    object Disease : MedicalData("Boli")

    @Parcelize
    object Treatment : MedicalData("Tratament")
}

@Parcelize
data class ParcelablePair(
    val user: User,
    val medicalRecord: MedicalRecord,
    val medicalData: MedicalData
) : Parcelable

class AddMedicalDataBottomSheet : BottomSheetDialogFragment() {
    private val medicalRecord by lazy {
        arguments?.getParcelable<ParcelablePair>(MEDICAL_RECORD)
            ?: throw IllegalArgumentException("Must have a valid user.")
    }
    private lateinit var parentActivity: MedicalRecordActivity
    private lateinit var binding: LayoutAddMedicalDataBinding
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
        parentActivity = requireActivity() as MedicalRecordActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutAddMedicalDataBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        setupListeners()
        setupObservers()
    }

    private fun initViews() {
        with(binding) {
            tvTitle.text = medicalRecord.medicalData.sectionName
            spMedicalData.adapter = MedicalDataArrayAdapter(
                requireContext(),
                R.layout.layout_spinner,
                when (medicalRecord.medicalData) {
                    MedicalData.Allergies -> resources.getStringArray(R.array.array_alergies)
                        .toList()
                    MedicalData.Disease -> resources.getStringArray(R.array.array_diseases)
                        .toList()
                    MedicalData.Treatment -> resources.getStringArray(R.array.array_treatments)
                        .toList()
                }
            )
        }
    }

    private fun setupListeners() {
        with(binding) {
            btnAddMedicalData.setOnClickListener {
                val medicalData = spMedicalData.selectedItem.toString()
                lifecycleScope.launch {
                    viewModel.addMedicalData(
                        medicalRecord.medicalRecord.id!!,
                        medicalData,
                        medicalRecord.medicalData,
                    ).collect { result ->
                        parentActivity.handleResponse(result) { result ->
                            if (result) {
                                setFragmentResult(
                                    BOTTOM_SHEET_DONE,
                                    Bundle().also { it.putBoolean(IS_SUCCESS, true) })
                                dismiss()
                            } else {
                                dismiss()
                                parentActivity.displayError()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupObservers() {}

    companion object {
        @JvmStatic
        fun newInstance(medicalRecord: ParcelablePair) = AddMedicalDataBottomSheet().apply {
            arguments = Bundle().apply { putParcelable(MEDICAL_RECORD, medicalRecord) }
        }
    }
}
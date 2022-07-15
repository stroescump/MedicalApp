package eu.ase.grupa1088.licenta.ui.medicalhistory

import MedicalRecordArrayAdapter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityMedicalRecordBinding
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedicalRecordActivity : BaseActivity() {
    override val binding by viewBinding { ActivityMedicalRecordBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MedicalRecordViewModel> {
        MedicalRecordViewModel.Factory(
            Dispatchers.IO
        )
    }
    private val user by lazy {
        intent.extras?.getParcelable<User>(USER_KEY)
            ?: throw IllegalArgumentException("Must receive a valid user.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchMedicalRecord()
        supportFragmentManager.setFragmentResultListener(
            BOTTOM_SHEET_DONE, this
        ) { requestKey, result ->
            run {
                if (requestKey == BOTTOM_SHEET_DONE && result.getBoolean(IS_SUCCESS, false)) {
                    getSpinnerAdapter().clear()
                    fetchMedicalRecord()
                }
            }
        }
    }

    private fun fetchMedicalRecord() {
        if (user.isDoctor().not()) {
            FirebaseAuth.getInstance().currentUser?.uid?.let { id ->
                lifecycleScope.launch {
                    viewModel.fetchMedicalRecordAsPatient(id)?.collect { result ->
                        handleResponse(result) {
                            binding.populateMedicalHistory(it)
                        }
                    }
                }
            }
        } else {
            lifecycleScope.launch {
                viewModel.fetchMedicalRecordAsDoctor(user.doctorID)?.collect { result ->
                    handleResponse(result) {
                        getSpinnerAdapter().insertData(it)
                    }
                }
            }
        }
    }

    override fun setupListeners() {
        with(binding) {
            btnFilterByDisease.setOnClickListener {
                handleFilterByDisease()
            }

            layoutContainerAlergies.setOnClickListener {
                handleOnClick(MedicalData.Allergies)
            }

            layoutContainerDiseaseHistory.setOnClickListener {
                handleOnClick(MedicalData.Disease)
            }

            layoutTreatmentsHistory.setOnClickListener {
                handleOnClick(MedicalData.Treatment)
            }

            btnBack.setOnClickListener { onBackPressed() }
            spPatients.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        arrayOf(
                            tvAllergiesList,
                            tvDiseasesList,
                            tvTreatmentsList
                        ).onEach { it.text = "" }
                        (spPatients.getItemAtPosition(position)?.let {
                            populateMedicalHistory((it as Pair<User, MedicalRecord>))
                        })
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }

            spDiseases.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        tvPatientsList.text = ""
                        (spDiseases.getItemAtPosition(position)?.let { disease ->
                            val patientsFilteredByDisease =
                                getSpinnerAdapter().getMedicalRecordList().filter {
                                    it.second.diseasesHistory?.contains(disease) == true
                                }.map { it.first?.nume.toString() }
                            if (patientsFilteredByDisease.isEmpty()) {
                                tvPatientsList.addData(getString(R.string.info_no_patient_with_symptoms))
                            } else {
                                tvPatientsList.addData(*patientsFilteredByDisease.toTypedArray())
                            }

                        })
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
        }
    }

    private fun ActivityMedicalRecordBinding.handleFilterByDisease() {
        if (isFilterMode()) {
            btnFilterByDisease.text = getString(R.string.filtreaza_pacientii_dupa_boala)
            arrayOf(
                spPatients,
                layoutContainerAlergies,
                layoutContainerDiseaseHistory,
                layoutTreatmentsHistory
            ).onEach { it.show() }
            spDiseases.hide()
            layoutContainerPatientsFiltered.hide()
        } else {
            btnFilterByDisease.text = getString(R.string.info_back_to_medical_record)
            arrayOf(
                spPatients,
                layoutContainerAlergies,
                layoutContainerDiseaseHistory,
                layoutTreatmentsHistory
            ).onEach { it.hide() }
            spDiseases.show()
            layoutContainerPatientsFiltered.show()
            spDiseases.adapter = MedicalDataArrayAdapter(
                this@MedicalRecordActivity,
                R.layout.layout_spinner,
                resources.getStringArray(R.array.array_diseases).toMutableList()
            )
        }
    }

    private fun ActivityMedicalRecordBinding.isFilterMode() =
        spDiseases.isVisible

    override fun initViews() {
        with(binding) {
            if (user.isDoctor().not()) {
                spPatients.hide()
                btnFilterByDisease.hide()
            } else {
                spPatients.adapter = initPatientsAdapter(mutableListOf())
            }
        }
    }

    private fun initPatientsAdapter(medicalRecordList: MutableList<Pair<User?, MedicalRecord>>) =
        MedicalRecordArrayAdapter(
            this@MedicalRecordActivity,
            R.layout.layout_spinner,
            medicalRecordList
        )

    override fun setupObservers() {}

    private fun getSpinnerAdapter() =
        (binding.spPatients.adapter as MedicalRecordArrayAdapter)

    private fun ActivityMedicalRecordBinding.handleOnClick(medicalData: MedicalData) {
        spPatients.selectedItem?.let {
            val (user, medicalRecord) = (it as Pair<User, MedicalRecord>)
            AddMedicalDataBottomSheet.newInstance(
                ParcelablePair(
                    user,
                    medicalRecord,
                    medicalData
                )
            ).show(supportFragmentManager, AddMedicalDataBottomSheet::class.java.simpleName)
        }
    }

    private fun ActivityMedicalRecordBinding.populateMedicalHistory(data: Pair<User?, MedicalRecord>) {
        data.let { (user, medicalRecord) ->
            medicalRecord.allergiesHistory?.let { allergies ->
                tvAllergiesList.addData(*allergies.toTypedArray())
            }
            medicalRecord.diseasesHistory?.let { diseases ->
                tvDiseasesList.addData(*diseases.toTypedArray())
            }
            medicalRecord.treatmentsHistory?.let { treatments ->
                tvTreatmentsList.addData(*treatments.toTypedArray())
            }
        }
    }

}

private fun User.isDoctor() = doctorID.isNullOrBlank().not()

private fun AppCompatTextView.addData(
    vararg newData: String
) {
    var dataHistory = ""
    newData.onEachIndexed { pos, it ->
        dataHistory = dataHistory.plus(
            this.text.toString().plus(
                "- ${it}${
                    if (pos < newData.lastIndex) "\n" else ""
                }"
            )
        )
    }
    text = dataHistory
}
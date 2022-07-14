package eu.ase.grupa1088.licenta.ui.medicalhistory

import MedicalRecordArrayAdapter
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityMedicalRecordBinding
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.USER_KEY
import eu.ase.grupa1088.licenta.utils.hide
import eu.ase.grupa1088.licenta.utils.viewBinding
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
            "TEST", this
        ) { requestKey, result ->
            run {
                if (requestKey == "TEST" && result.getBoolean("IS_SUCCESS", false)) {
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
            layoutContainerAlergies.setOnClickListener {
                spPatients.selectedItem?.let {
                    val (user, medicalRecord) = (it as Pair<User, MedicalRecord>)
                    AddMedicalDataBottomSheet.newInstance(
                        ParcelablePair(
                            user,
                            medicalRecord,
                            MedicalData.Allergies
                        )
                    ).apply {
                        show(
                            supportFragmentManager,
                            AddMedicalDataBottomSheet::class.java.simpleName
                        )
                    }
                }
            }

            layoutContainerDiseaseHistory.setOnClickListener {
                spPatients.selectedItem?.let {
                    val (user, medicalRecord) = (it as Pair<User, MedicalRecord>)
                    AddMedicalDataBottomSheet.newInstance(
                        ParcelablePair(
                            user,
                            medicalRecord,
                            MedicalData.Disease
                        )
                    ).show(supportFragmentManager, AddMedicalDataBottomSheet::class.java.simpleName)
                }
            }

            layoutTreatmentsHistory.setOnClickListener {
                spPatients.selectedItem?.let {
                    val (user, medicalRecord) = (it as Pair<User, MedicalRecord>)
                    AddMedicalDataBottomSheet.newInstance(
                        ParcelablePair(
                            user,
                            medicalRecord,
                            MedicalData.Treatment
                        )
                    ).show(supportFragmentManager, AddMedicalDataBottomSheet::class.java.simpleName)
                }
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

    override fun initViews() {
        with(binding) {
            if (user.isDoctor().not()) {
                binding.spPatients.hide()
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
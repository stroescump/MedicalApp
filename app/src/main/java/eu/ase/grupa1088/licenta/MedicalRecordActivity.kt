package eu.ase.grupa1088.licenta

import androidx.appcompat.widget.AppCompatTextView
import eu.ase.grupa1088.licenta.databinding.ActivityMedicalRecordBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.viewBinding

class MedicalRecordActivity : BaseActivity() {
    override val binding by viewBinding { ActivityMedicalRecordBinding.inflate(layoutInflater) }

    override fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun initViews() {
        with(binding) {
            tvAllergiesList.apply {
                addData(
                    "Amoxicilina",
                    "Ampicilina",
                    "Penicilina G",
                    "Penicilina V"
                )
            }
            tvDiseasesList.apply {
                addData(
                    "Diabetul zaharat",
                    "Obezitate",
                    "Artroza",
                    "Scolioza",
                    "Durerile musculare"
                )
            }
            tvTreatmentsList.apply {
                addData(
                    "Fiziokinetoterapie",
                    "Electroterapie",
                    "Hidroterapie",
                    "Tomografie"
                )
            }
        }
    }

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

    override fun setupObservers() {}
}
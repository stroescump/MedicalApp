package eu.ase.grupa1088.licenta.ui.medicalhistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.db.williamchart.ExperimentalFeature
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.LayoutStatisticsBottomSheetBinding
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.utils.MEDICAL_RECORD
import eu.ase.grupa1088.licenta.utils.displayInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt


class StatisticsBottomSheetFragment : BottomSheetDialogFragment() {
    private val medicalRecord by lazy {
        arguments?.getParcelableArrayList<MedicalRecord>(MEDICAL_RECORD)
            ?: throw IllegalArgumentException("Must have a valid user.")
    }
    private lateinit var binding: LayoutStatisticsBottomSheetBinding

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        expandBottomSheet()
        binding = LayoutStatisticsBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    @OptIn(ExperimentalFeature::class)
    private fun initViews() {
        with(binding) {
            lifecycleScope.launch(Dispatchers.IO) {
                val diseasesList =
                    resources.getStringArray(R.array.array_diseases).toMutableList()
                val dataEntries = diseasesList.map { disease ->
                    disease to medicalRecord.count {
                        it.diseasesHistory?.contains(disease) == true
                    }.toFloat()
                }
                withContext(Dispatchers.Main) {
                    barChart.apply {
                        labelsFormatter = { it.roundToInt().toString() }
                        labelsSize = 18F
                        show(dataEntries.map {
                            it.first.toCharArray().first().toString() to it.second
                        })
                        onDataPointTouchListener = { index: Int, _: Float, _: Float ->
                            displayInfo("Incidenta ${dataEntries[index].first.lowercase()} - ${dataEntries[index].second.toInt()} cazuri")
                        }
                    }
                }
            }
        }
    }

    private fun expandBottomSheet() {
        val d = requireDialog() as BottomSheetDialog
        d.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {
        @JvmStatic
        fun newInstance(medicalRecord: ArrayList<MedicalRecord>) =
            StatisticsBottomSheetFragment().apply {
                arguments =
                    Bundle().apply { putParcelableArrayList(MEDICAL_RECORD, medicalRecord) }
            }
    }
}
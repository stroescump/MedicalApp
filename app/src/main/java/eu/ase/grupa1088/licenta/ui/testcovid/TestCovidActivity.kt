package eu.ase.grupa1088.licenta.ui.testcovid

import androidx.appcompat.app.AlertDialog
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityTestCovidBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.dashboard.ProfileActivity
import eu.ase.grupa1088.licenta.utils.viewBinding

class TestCovidActivity : BaseActivity() {
    override val binding by viewBinding(ActivityTestCovidBinding::inflate)
    private val arrayOfQuestionItems by lazy {
        with(binding) {
            arrayOf(
                covidQuestion1,
                covidQuestion2,
                covidQuestion3,
                covidQuestion4,
                covidQuestion5,
                covidQuestion6,
                covidQuestion7,
                covidQuestion8,
                covidQuestion9,
                covidQuestion10,
                covidQuestion11,
                covidQuestion12,
            )
        }
    }

    override fun initViews() {
        arrayOfQuestionItems.onEachIndexed { index, covidQuestionItem ->
            val questionsArray = resources.getStringArray(R.array.test_covid_online_questions)
            if (questionsArray.size > index) {
                covidQuestionItem.text = questionsArray[index]
            }
        }
    }

    override fun setupListeners() {
        with(binding) {
            btnGetScreeningResults.setOnClickListener { handleGetScreeningResults() }
        }
    }

    private fun handleGetScreeningResults() {
        var scoring = 0
        arrayOfQuestionItems.onEach {
            try {
                scoring += it.getResponse()
            } catch (e: IllegalArgumentException) {
                displayError(getString(R.string.error_one_response_mandatory_per_question))
            }
        }
        AlertDialog.Builder(this@TestCovidActivity)
            .setMessage(
                if (scoring >= 6) {
                    getString(R.string.please_remain_isolated)
                } else {
                    getString(R.string.patient_triage_safe)
                }
            ).setButton(
                AlertDialogButton.PositiveButton
            ) {
                navigateTo(ProfileActivity::class.java, true)
            }.create().show()
    }

    override fun setupObservers() {}
}
package eu.ase.grupa1088.licenta.ui.dashboard

import MedicalAppointmentAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import eu.ase.grupa1088.licenta.MedicalRecordActivity
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.FragmentDashboardBinding
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.ui.appointments.AppointmentActivity
import eu.ase.grupa1088.licenta.ui.dashboard.DashboardFragment.DashboardItem.*
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.ui.testcovid.TestCovidActivity
import eu.ase.grupa1088.licenta.utils.USER_KEY
import eu.ase.grupa1088.licenta.utils.getParentActivity
import eu.ase.grupa1088.licenta.utils.navigateTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {
    private lateinit var binding: FragmentDashboardBinding
    private val user by lazy {
        arguments?.getParcelable<User>(USER_KEY)
            ?: throw IllegalStateException("Must have a valid user.")
    }
    private val viewModel by activityViewModels<AccountViewModel>()
    private lateinit var parentActivity: ProfileActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = getParentActivity(ProfileActivity::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        getAdapter().clear()
        viewModel.getUserAppointments()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.medicalAppointmentStateFlow.collect {
                withContext(Dispatchers.Main) {
                    parentActivity.handleResponse(it) {
                        getAdapter().addAppointment(it)
                    }
                }
            }
        }

        viewModel.medicalAppointmentLiveData.observe(viewLifecycleOwner) {
            parentActivity.handleResponse(it) { medicalAppointments ->
                getAdapter().refreshList(medicalAppointments)
            }
        }
        viewModel.deleteLiveData.observe(viewLifecycleOwner) {
            parentActivity.handleResponse(it) { pos ->
                getAdapter().removeItem(pos)
            }
        }
    }

    private fun getAdapter() = (binding.rvMedicalAppointments.adapter as MedicalAppointmentAdapter)

    private fun initViews() {
        with(binding) {
            rvCategories.adapter = ItemDashboardAdapter(
                mutableListOf(
                    Programari,
                    TestCovidOnline,
                    Medici,
                    IstoricMedical,
                )
            ) { dashboardItem ->
                when (dashboardItem) {
                    Medici -> {}
                    Programari -> navigateTo(AppointmentActivity::class.java)
                    IstoricMedical -> navigateTo(MedicalRecordActivity::class.java)
                    TestCovidOnline -> navigateTo(TestCovidActivity::class.java)
                }
            }
            rvMedicalAppointments.adapter =
                MedicalAppointmentAdapter(
                    mutableListOf(),
                    viewModel.isDoctor
                ) { medicalAppointment, pos ->
                    viewModel.deleteAppointment(medicalAppointment, pos)
                }
        }
    }

    companion object {
        fun newInstance(user: User): DashboardFragment {
            val fragment = DashboardFragment()
            fragment.arguments = Bundle().also { it.putParcelable(USER_KEY, user) }
            return fragment
        }
    }

    sealed class DashboardItem(val name: String, val resInt: Int) {
        object Programari : DashboardItem("Programari", R.drawable.appointment)
        object TestCovidOnline : DashboardItem("Test covid online", R.drawable.mask)
        object Medici : DashboardItem("Medici", R.drawable.doctors)
        object IstoricMedical : DashboardItem("Istoric medical", R.drawable.symptoms)
    }
}
package eu.ase.grupa1088.licenta.ui.dashboard

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityProfileActivityBinding
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.AccountService
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.login.LoginActivity
import eu.ase.grupa1088.licenta.ui.profile.ProfileDetailsActivity
import eu.ase.grupa1088.licenta.ui.register.AccountViewModel
import eu.ase.grupa1088.licenta.ui.testcovid.TestCovidActivity
import eu.ase.grupa1088.licenta.utils.USER_KEY
import eu.ase.grupa1088.licenta.utils.capitalizeWord
import eu.ase.grupa1088.licenta.utils.viewBinding
import kotlinx.coroutines.launch

class ProfileActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var user: User
    override val binding by viewBinding(ActivityProfileActivityBinding::inflate)
    private val viewModel by viewModels<AccountViewModel> {
        AccountViewModel.Factory(AccountService(FirebaseAuth.getInstance()))
    }

    private lateinit var drawerFullName: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUserInfo()
        user = intent.extras?.getParcelable(USER_KEY)
            ?: throw IllegalStateException("Must have a valid user.")
    }

    override fun setupListeners() {}

    override fun initViews() {
        with(binding) {
            val toggle = ActionBarDrawerToggle(
                this@ProfileActivity, binding.drawerLayout,
                binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            ).also {
                it.drawerArrowDrawable = DrawerArrowDrawable(this@ProfileActivity).apply {
                    color = resources.getColor(R.color.red, theme)
                }
                it.syncState()
            }
            drawerLayout.addDrawerListener(toggle)
            navView.apply {
                getHeaderView(0).also {
                    drawerFullName = it.findViewById(R.id.nav_user_fullname)
                    it.findViewById<CircleImageView>(R.id.nav_user_image).setOnClickListener {
                        navigateTo(ProfileDetailsActivity::class.java)
                    }
                    it.findViewById<AppCompatImageButton>(R.id.btnClose).setOnClickListener {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                }
                setNavigationItemSelectedListener(this@ProfileActivity)
            }
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiStateFlow.collect {
                handleResponse(it) { user -> provideUserInfoSuccessHandler(user) }
            }
        }
    }

    private fun provideUserInfoSuccessHandler(user: User?) {
        user?.let { userSafe ->
            viewModel.isDoctor =
                user.doctorID.isNullOrBlank().not() && user.speciality.isNullOrBlank().not()
            viewModel.doctorID = user.doctorID.toString()
            this.user = user
            replaceFragment(binding.fragmentContainer.id, DashboardFragment.newInstance(user))
            userSafe.nume?.let { name ->
                var nume = ""
                var prenume = ""
                name.split(" ").apply {
                    nume = first()
                    prenume = filter { it != nume }.joinToString(" ")
                }
                val numeFormatat =
                    "${if (viewModel.isDoctor) "Dr." else ""} ${nume.capitalizeWord()}\n${prenume.capitalizeWord()}"
                drawerFullName.text = numeFormatat
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> replaceFragment(
                binding.fragmentContainer.id,
                DashboardFragment.newInstance(user)
            )
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                navigateTo(LoginActivity::class.java, true)
            }
            R.id.profile -> navigateTo(ProfileDetailsActivity::class.java)
            R.id.testCovid -> navigateTo(TestCovidActivity::class.java)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
package eu.ase.grupa1088.licenta.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ActivityProfileActivityBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.login.LoginActivity
import eu.ase.grupa1088.licenta.ui.profile.ProfileDetailsActivity
import eu.ase.grupa1088.licenta.utils.viewBinding
import java.util.*

class ProfileActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override val binding by viewBinding(ActivityProfileActivityBinding::inflate)

    private var userRef: DatabaseReference? = null
    private lateinit var drawerFullName: AppCompatTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager.beginTransaction()
            .replace(
                binding.fragmentContainer.id,
                DashboardFragment(),
                DashboardFragment::class.java.simpleName
            ).commit()
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
                }

                setNavigationItemSelectedListener(this@ProfileActivity)
                setCheckedItem(R.id.home)
            }
        }

        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )

        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    snapshot.child("nume").value.toString()
                        .also {
                            val (nume, prenume) = it.split(" ")
                            val numeFormatat = "${nume.capitalize()}\n${prenume.capitalize()}"
                            drawerFullName.text = numeFormatat
                        }
//                    val mail = snapshot.child("email").value.toString()
//                    nav_email.setText(mail)
//                    val phone = snapshot.child("nrTel").value.toString()
//                    nav_phone.setText(phone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                displayError(error.message)
            }
        })
    }

    private fun String.capitalize(): String = replaceFirstChar { char ->
        if (char.isLowerCase()) char.titlecase(
            Locale.getDefault()
        ) else char.toString()
    }

    override fun setupObservers() {}

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                DashboardFragment()
            ).commit()
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            }
            R.id.profile -> navigateTo(ProfileDetailsActivity::class.java)
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
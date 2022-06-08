package eu.ase.grupa1088.licenta

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewbinding.ViewBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.ui.login.LoginActivity

class ProfileActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    //pt bara
    private var drawerLayout: DrawerLayout? = null
    private var toolbar: Toolbar? = null
    private lateinit var nav_view: NavigationView
    private val btn1: LinearLayout? = null

    //private CircleImageView nav_profile_image;
    private lateinit var nav_fullname: TextView
    private lateinit var nav_email: TextView
    private lateinit var nav_phone: TextView
    private lateinit var nav_cnp: TextView
    private var userRef: DatabaseReference? = null
    private val logout: Button? = null
    override val binding: ViewBinding
        get() = TODO("Not yet implemented")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_activity)


        //pt bara
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Centru medical de recuperare"
        drawerLayout = findViewById<DrawerLayout?>(R.id.drawerLayout).also {
            val toggle = ActionBarDrawerToggle(
                this@ProfileActivity, drawerLayout,
                toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
            it.addDrawerListener(toggle)
            toggle.syncState()
        }
        nav_view = findViewById<NavigationView?>(R.id.nav_view).also {
            it.setNavigationItemSelectedListener(this)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MessageFragment()
            ).commit()
            nav_view.setCheckedItem(R.id.home)
        }


        //nav_profile_image=nav_view.getHeaderView(0).findViewById(R.id.nav_user_image);
        nav_fullname = nav_view.getHeaderView(0).findViewById(R.id.nav_user_fullname)
        nav_email = nav_view.getHeaderView(0).findViewById(R.id.nav_user_email)
        nav_phone = nav_view.getHeaderView(0).findViewById(R.id.nav_user_phone)
        nav_cnp = nav_view.getHeaderView(0).findViewById(R.id.nav_user_cnp)

        //luam datele din firebase
        userRef = FirebaseDatabase.getInstance().reference.child("Users").child(
            FirebaseAuth.getInstance().currentUser!!.uid
        )
        userRef!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("nume").value.toString()
                    nav_fullname.setText(name)
                    val mail = snapshot.child("email").value.toString()
                    nav_email.setText(mail)

//                    String cnp=snapshot.child("cnp").getValue().toString();
//                    nav_email.setText(cnp);
//
                    val phone = snapshot.child("nrTel").value.toString()
                    nav_phone.setText(phone)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })


//pt logout- nu mai e nevoie de el-> avem logout in navigation bar
//   logout=(Button) findViewById(R.id.singOutBtn);
//   logout.setOnClickListener(new View.OnClickListener() {
//       @Override
//       public void onClick(View view) {
//           FirebaseAuth.getInstance().signOut();
//           startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
//       }
//   });
    }

    override fun setupListeners() {
        TODO("Not yet implemented")
    }

    override fun initViews() {
        TODO("Not yet implemented")
    }

    override fun setupObservers() {
        TODO("Not yet implemented")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> supportFragmentManager.beginTransaction().replace(
                R.id.fragment_container,
                MessageFragment()
            ).commit()
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@ProfileActivity, LoginActivity::class.java))
            }
        }
        drawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }
}
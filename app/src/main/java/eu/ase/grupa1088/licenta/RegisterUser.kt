package eu.ase.grupa1088.licenta

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import eu.ase.grupa1088.licenta.R
import android.content.Intent
import android.util.Patterns
import eu.ase.grupa1088.licenta.RegisterUser
import eu.ase.grupa1088.licenta.SchimbaParola
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import eu.ase.grupa1088.licenta.ProfileActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.database.DatabaseReference
import eu.ase.grupa1088.licenta.MessageFragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import eu.ase.grupa1088.licenta.LoginActivity
import androidx.core.view.GravityCompat

class RegisterUser : AppCompatActivity(), View.OnClickListener {
    private var banner: TextView? = null
    private var registerUser: TextView? = null
    private var etNume: EditText? = null
    private var etNrTel: EditText? = null
    private var etEmail: EditText? = null
    private var etParola: EditText? = null
    private var etCnp: EditText? = null
    private var progressBar: ProgressBar? = null
    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user)
        mAuth = FirebaseAuth.getInstance()
        banner = findViewById<View>(R.id.registerLogo) as TextView
        banner!!.setOnClickListener(this)
        registerUser = findViewById<View>(R.id.RegisterBtn) as Button
        registerUser!!.setOnClickListener(this)
        etNume = findViewById<View>(R.id.registernume) as EditText
        etNrTel = findViewById<View>(R.id.registerTelefon) as EditText
        etEmail = findViewById<View>(R.id.registerEmail) as EditText
        etParola = findViewById<View>(R.id.registerParola) as EditText
        etCnp = findViewById<View>(R.id.registerCnp) as EditText
        progressBar = findViewById<View>(R.id.registerProgressBar) as ProgressBar
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.registerLogo -> startActivity(Intent(this, LoginActivity::class.java))
            R.id.RegisterBtn -> registerUser()
        }
    }

    private fun registerUser() {
        //punem validari
        //transformam inputurile in string
        val email = etEmail!!.text.toString().trim { it <= ' ' }
        val parola = etParola!!.text.toString().trim { it <= ' ' }
        val nume = etNume!!.text.toString().trim { it <= ' ' }
        val telefon = etNrTel!!.text.toString().trim { it <= ' ' }
        val cnp = etCnp!!.text.toString().trim { it <= ' ' }
        if (nume.isEmpty()) {
            etNume!!.error = "Trebuie introdus numele"
            etNume!!.requestFocus()
            return
        }
        if (telefon.isEmpty()) {
            etNrTel!!.error = "Trebuie introdus numarul de telefon"
            etNrTel!!.requestFocus()
            return
        }
        if (email.isEmpty()) {
            etEmail!!.error = "Trebuie introdus emailul"
            etEmail!!.requestFocus()
            return
        }
        //pt validarea emailului daca e valid
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail!!.error = "Introduceti un email valid!"
            etEmail!!.requestFocus()
            return
        }
        if (parola.isEmpty()) {
            etParola!!.error = "Trebuie introdusa parola!"
            etParola!!.requestFocus()
            return
        }
        //validare pt parola care nu are mai putin de 6 caractere
        if (parola.length < 6) {
            etParola!!.error = "Parola trebuie sa aiba minim 6 caractere"
            etParola!!.requestFocus()
            return
        }
        if (cnp.isEmpty()) {
            etCnp!!.error = "Trebuie introdus cnp-ul!"
            etCnp!!.requestFocus()
            return
        }
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.createUserWithEmailAndPassword(email, parola)
            .addOnCompleteListener { task ->
                //verificam daca userul e register
                if (task.isSuccessful) {
                    val user = User(nume, telefon, email, parola, cnp)
                    //trimitem obiectele user catre database
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                        .setValue(user).addOnCompleteListener { task ->
                            //verificam daca taskul e cu succes
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    this@RegisterUser,
                                    "Userul a fost inregistrat cu succes",
                                    Toast.LENGTH_LONG
                                ).show()
                                progressBar!!.visibility = View.GONE
                                //redirectionam inapoi catre pagina de login
                            } else {
                                Toast.makeText(
                                    this@RegisterUser,
                                    "Nu s-a putut inregistra userul! Incercati iar",
                                    Toast.LENGTH_LONG
                                ).show()
                                //o punem pe gone
                                progressBar!!.visibility = View.GONE
                            }
                        }
                } else {
                    Toast.makeText(
                        this@RegisterUser,
                        "Nu s-a putut inregistra userul! Incercati iar",
                        Toast.LENGTH_LONG
                    ).show()
                    //o punem pe gone
                    progressBar!!.visibility = View.GONE
                }
            }
    }
}
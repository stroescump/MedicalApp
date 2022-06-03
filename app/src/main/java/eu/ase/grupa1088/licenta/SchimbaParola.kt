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

class SchimbaParola : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    private var etEmail: EditText? = null
    private var resetParolabtn: Button? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schimba_parola)
        etEmail = findViewById<View>(R.id.etemail) as EditText
        resetParolabtn = findViewById<View>(R.id.btnResetPassword) as Button
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        auth = FirebaseAuth.getInstance()
        resetParolabtn!!.setOnClickListener { resetParola() }
    }

    private fun resetParola() {
        val email = etEmail!!.text.toString().trim { it <= ' ' }
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
        progressBar!!.visibility = View.VISIBLE
        auth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    this@SchimbaParola,
                    "Verifica mail pentru resetare parola",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this@SchimbaParola, "Eroare!Incearca iar!", Toast.LENGTH_LONG).show()
            }
        }
    }
}
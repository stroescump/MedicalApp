package eu.ase.grupa1088.licenta

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*

class SchimbaParola : AppCompatActivity() {
    var auth: FirebaseAuth? = null
    private var etEmail: EditText? = null
    private var resetParolabtn: Button? = null
    private var progressBar: ProgressBar? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schimba_parola)
        etEmail = findViewById<View>(R.id.etEmail) as EditText
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
package eu.ase.grupa1088.licenta

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var etEmail: EditText? = null
    private var etParola: EditText? = null
    private var signIn: Button? = null
    private var register: TextView? = null
    private var uitatParola: TextView? = null
    private var mAuth: FirebaseAuth? = null
    private var progressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register = findViewById<View>(R.id.tvRegister) as TextView
        register!!.setOnClickListener(this)
        signIn = findViewById<View>(R.id.btnSignIn) as Button
        signIn!!.setOnClickListener(this)
        etEmail = findViewById<View>(R.id.etemail) as EditText
        etParola = findViewById<View>(R.id.etpassword) as EditText
        progressBar = findViewById<View>(R.id.progressBar) as ProgressBar
        mAuth = FirebaseAuth.getInstance()
        uitatParola = findViewById<View>(R.id.tvForgotPassword) as TextView
        uitatParola!!.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.tvRegister -> //                startActivity(new Intent(this,RegisterUser.class));
                startActivity(Intent(this, RegisterUser::class.java))
            R.id.btnSignIn -> userLogin()
            R.id.tvForgotPassword -> startActivity(Intent(this, SchimbaParola::class.java))
        }
    }

    private fun userLogin() {
        val email = etEmail!!.text.toString().trim { it <= ' ' }
        val parola = etParola!!.text.toString().trim { it <= ' ' }
        if (email.isEmpty()) {
            etEmail!!.error = "Va rog introduceti emailul"
            etEmail!!.requestFocus()
            return
        }
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
        progressBar!!.visibility = View.VISIBLE
        mAuth!!.signInWithEmailAndPassword(email, parola)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        //redirect to user profile
                        startActivity(Intent(this@LoginActivity, ProfileActivity::class.java))
                    } else {
                        user.sendEmailVerification()
                        Toast.makeText(this@LoginActivity, "Verifica emailul", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Nu s-a reusit logarea", Toast.LENGTH_LONG)
                        .show()
                }
            }
    } //    @Override
    //    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    //        switch(item.getItemId())
    //        {
    //            case R.id.logout:
    //                FirebaseAuth.getInstance().signOut();
    //                startActivity(new Intent(this, LoginActivity.class));
    //        break;
    //
    //        }
    //        return false;
    //    }
}
package eu.ase.grupa1088.licenta.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import eu.ase.grupa1088.licenta.User
import eu.ase.grupa1088.licenta.utils.AppResult

class AccountService(val firebaseDep: FirebaseAuth) {

    fun registerUser(
        email: String,
        parola: String,
        nume: String,
        cnp: String,
        telefon: String,
        completionCallback: (user: AppResult<User>) -> Unit
    ) {
        firebaseDep.createUserWithEmailAndPassword(email, parola)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(nume, telefon, email, parola, cnp)
                    FirebaseAuth.getInstance().currentUser?.let {
                        FirebaseDatabase.getInstance().getReference("Users")
                            .child(it.uid)
                            .setValue(user).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    completionCallback(AppResult.Success(user))
                                } else {
                                    completionCallback(
                                        AppResult.Error(
                                            task.exception
                                                ?: Throwable("Something went wrong. Please retry.")
                                        )
                                    )
                                }
                            }
                    }
                } else {
                    completionCallback(
                        AppResult.Error(
                            task.exception
                                ?: Throwable("Something went wrong. Please retry.")
                        )
                    )
                }
            }
    }
}
package eu.ase.grupa1088.licenta.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.FirebaseEndpoints.MedicalAppointments
import eu.ase.grupa1088.licenta.repo.FirebaseEndpoints.Users
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.getAppointments
import eu.ase.grupa1088.licenta.utils.getUser

sealed class FirebaseEndpoints(val path: String) {
    object Users : FirebaseEndpoints("Users")
    object MedicalAppointments : FirebaseEndpoints("Appointments")
    object Doctors : FirebaseEndpoints("Doctors")
}

fun getUserAccountDetails(completionHandler: (user: AppResult<User>) -> Unit) =
    getFirebaseRoot().child(Users.path).child(
        getCurrentUserUID() ?: throwUIDException()
    ).get().addOnSuccessListener {
        it.getUser()?.let { user ->
            completionHandler(AppResult.Success(user))
        }
            ?: completionHandler(AppResult.Error(IllegalArgumentException("No user for requested input. Please contact owner of the app.")))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

fun getUserAppointmentsFirebase(completionHandler: (appointments: AppResult<List<MedicalAppointment>>) -> Unit) =
    getFirebaseRoot().child(MedicalAppointments.path).child(
        getCurrentUserUID() ?: throwUIDException()
    ).get().addOnSuccessListener { dataSnapshot ->
        dataSnapshot.getAppointments()?.let { appointmentHashMap ->
            completionHandler(AppResult.Success(prepareMedicalAppointments(appointmentHashMap)))
        }
            ?: completionHandler(AppResult.Error(IllegalArgumentException("No medical appointments for requested input. Please contact owner of the app.")))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

fun deleteAppointmentFirebase(
    appointmentId: String,
    pos: Int,
    completionHandler: (AppResult<Int>) -> Unit
) =
    getFirebaseRoot().child(MedicalAppointments.path).child(
        getCurrentUserUID()
            ?: throwUIDException()
    ).child(appointmentId).removeValue().addOnSuccessListener {
        completionHandler(AppResult.Success(pos))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

private fun prepareMedicalAppointments(user: HashMap<String, MedicalAppointment>) =
    user.entries.map { appointment ->
        appointment.value.copy(id = appointment.key)
    }.toList()

private fun throwUIDException(): Nothing =
    throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")


private fun getFirebaseRoot() = FirebaseDatabase.getInstance().reference

private fun getCurrentUserUID() = FirebaseAuth.getInstance().currentUser?.uid
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
}

fun getUserAccountDetails(completionHandler: (user: AppResult<User>) -> Unit) =
    FirebaseDatabase.getInstance().reference.child(Users.path).child(
        FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")
    ).get().addOnSuccessListener {
        it.getUser()?.let { user ->
            completionHandler(AppResult.Success(user))
        }
            ?: completionHandler(AppResult.Error(IllegalArgumentException("No user for requested input. Please contact owner of the app.")))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

fun getUserAppointmentsFirebase(completionHandler: (appointments: AppResult<List<MedicalAppointment>>) -> Unit) =
    FirebaseDatabase.getInstance().reference.child(MedicalAppointments.path).child(
        FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")
    ).get().addOnSuccessListener { dataSnapshot ->
        dataSnapshot.getAppointments()?.let { user ->
            completionHandler(AppResult.Success(user.entries.map { appointment ->
                appointment.value.copy(id = appointment.key)
            }.toList()))
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
    FirebaseDatabase.getInstance().reference.child(MedicalAppointments.path).child(
        FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")
    ).child(appointmentId).removeValue().addOnSuccessListener {
        completionHandler(AppResult.Success(pos))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }
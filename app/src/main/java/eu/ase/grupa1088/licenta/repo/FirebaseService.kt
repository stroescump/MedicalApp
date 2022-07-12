package eu.ase.grupa1088.licenta.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.FirebaseEndpoints.*
import eu.ase.grupa1088.licenta.utils.AppResult
import eu.ase.grupa1088.licenta.utils.getAllUsers
import eu.ase.grupa1088.licenta.utils.getAppointments
import eu.ase.grupa1088.licenta.utils.getUser

sealed class FirebaseEndpoints(val path: String) {
    object Users : FirebaseEndpoints("Users")
    object MedicalAppointments : FirebaseEndpoints("Appointments")
    data class Doctors(val bookedSlots: String = "bookedSlots") : FirebaseEndpoints("Doctors")
}

fun getDoctors(speciality: String, completionHandler: (user: AppResult<List<User>>) -> Unit) =
    getFirebaseRoot().child(Users.path).get().addOnSuccessListener { snapshot ->
        snapshot.getAllUsers()?.let { user ->
            completionHandler(AppResult.Success(user.values.filter {
                it.doctorID.isNullOrBlank().not() && it.speciality.isNullOrBlank()
                    .not() && it.speciality == speciality
            }))
        }
            ?: completionHandler(AppResult.Error(IllegalArgumentException("No doctors have joined the app. Get some doctors onboard, they can take care of your patients.")))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

fun getUserAccountDetails(completionHandler: (user: AppResult<User>) -> Unit) = run {
    completionHandler(AppResult.Progress)
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
}

fun getUserAppointmentsFirebase(completionHandler: (appointments: AppResult<List<MedicalAppointment>>) -> Unit) =
    run {
        completionHandler(AppResult.Progress)
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
    }

fun deleteAppointmentFirebase(
    medicalAppointment: MedicalAppointment,
    pos: Int,
    completionHandler: (AppResult<Int>) -> Unit
) = medicalAppointment.id?.let { appointmentID ->
    medicalAppointment.doctorID?.let { doctorID ->
        medicalAppointment.date?.let { appointmentDate ->
            completionHandler(AppResult.Progress)
            val formattedAppointmentDate = appointmentDate.split(".").joinToString("")
            getFirebaseRoot().child(MedicalAppointments.path).child(
                getCurrentUserUID()
                    ?: throwUIDException()
            ).child(appointmentID).removeValue().addOnSuccessListener {
                getDoctorAppointmentNode(doctorID).child(formattedAppointmentDate)
                    .child(appointmentID).removeValue()
                    .addOnSuccessListener {
                        completionHandler(AppResult.Success(pos))
                    }.addOnFailureListener {
                        completionHandler(AppResult.Error(it))
                    }
            }.addOnFailureListener {
                completionHandler(AppResult.Error(it))
            }
        }
    }
}

fun getDoctorAvailability(
    doctorID: String,
    date: String,
    completionHandler: (AppResult<List<MedicalAppointment>>) -> Unit
) {
    val formattedAppointmentDate = date.split(".").joinToString("")
    completionHandler(AppResult.Progress)
    getDoctorAppointmentNode(doctorID).child(formattedAppointmentDate).get()
        .addOnSuccessListener { appointmentsHashMap ->
            appointmentsHashMap.children.onEach { appointment ->
                val userID = appointment.child("patient_key").value.toString()
                getFirebaseRoot().child(MedicalAppointments.path).child(userID).get()
                    .addOnSuccessListener { dataSnapshot ->
                        dataSnapshot.getAppointments()?.let { appointmentHashMap ->
                            completionHandler(
                                AppResult.Success(
                                    prepareMedicalAppointments(
                                        appointmentHashMap
                                    )
                                )
                            )
                        }
                            ?: completionHandler(AppResult.Error(IllegalArgumentException("No medical appointments for requested input. Please contact owner of the app.")))
                    }.addOnFailureListener {
                        completionHandler(AppResult.Error(it))
                    }
            }
        }.addOnFailureListener {
            completionHandler(AppResult.Error(it))
        }
}

private fun prepareMedicalAppointments(user: HashMap<String, MedicalAppointment>) =
    user.entries.map { appointment ->
        appointment.value.copy(id = appointment.key)
    }.toList()

private fun throwUIDException(): Nothing =
    throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")


private fun getFirebaseRoot() = FirebaseDatabase.getInstance().reference

private fun getDoctorAppointmentNode(doctorID: String) = Doctors().run {
    getFirebaseRoot().child(path).child(doctorID).child(bookedSlots)
}

private fun getCurrentUserUID() = FirebaseAuth.getInstance().currentUser?.uid
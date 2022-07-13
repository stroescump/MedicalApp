package eu.ase.grupa1088.licenta.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.FirebaseEndpoints.*
import eu.ase.grupa1088.licenta.utils.*

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

fun getUserAppointmentsFirebase(
    isDoctor: Boolean,
    doctorID: String,
    completionHandlerPatient: (AppResult<List<MedicalAppointment>>) -> Unit,
    completionHandlerDoctor: (AppResult<MedicalAppointment>) -> Unit
) = run {
    if (isDoctor) {
        completionHandlerDoctor(AppResult.Progress)
        getDoctorAppointments(doctorID, completionHandlerDoctor)
    } else {
        completionHandlerPatient(AppResult.Progress)
        getPatientAppointments(completionHandlerPatient)
    }
}

private fun getPatientAppointments(completionHandler: (AppResult<List<MedicalAppointment>>) -> Unit) =
    getFirebaseRoot().child(MedicalAppointments.path).child(
        getCurrentUserUID() ?: throwUIDException()
    ).get().addOnSuccessListener { dataSnapshot ->
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

private fun getDoctorAppointments(
    doctorID: String,
    completionHandler: (AppResult<MedicalAppointment>) -> Unit
) = getDoctorAppointmentNode(doctorID).get()
    .addOnSuccessListener { snapshotByDate ->
        mutableListOf<MedicalAppointment>()
        if (snapshotByDate.hasChildren()) {
            snapshotByDate.children.onEach { patientSnapshot ->
                val patientId = patientSnapshot.children.first().key.toString()
                val appointmentId =
                    patientSnapshot.children.first().children.first().value.toString()
                getFirebaseRoot().child(Users.path).child(patientId).get()
                    .addOnSuccessListener { userDetailsSnapshot ->
                        val userName =
                            (userDetailsSnapshot.value as HashMap<*, *>)["nume"].toString()
                        getFirebaseRoot().child(MedicalAppointments.path).child(patientId)
                            .child(appointmentId).get()
                            .addOnSuccessListener { appointmentDetails ->
                                appointmentDetails.getOneAppointment()?.copy(
                                    name = userName
                                )?.let {
                                    completionHandler(AppResult.Success(it))
                                }
                            }
                            .addOnFailureListener {
                                completionHandler(AppResult.Error(it))
                            }
                    }.addOnFailureListener {
                        completionHandler(AppResult.Error(it))
                    }
            }
        } else completionHandler(AppResult.Success(null))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
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
            if (appointmentsHashMap.hasChildren()) {
                appointmentsHashMap.children.onEach { appointment ->
                    val userID = appointment.key.toString()
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
                                ?: completionHandler(AppResult.Success(listOf()))
                        }.addOnFailureListener {
                            completionHandler(AppResult.Error(it))
                        }
                }
            } else completionHandler(AppResult.Success(listOf()))
        }.addOnFailureListener {
            completionHandler(AppResult.Error(it))
        }
}

fun sendAppointment(
    patientId: String,
    appointment: MedicalAppointment,
    completionHandler: (AppResult<Boolean>) -> Unit
) {
    completionHandler(AppResult.Progress)
    val formattedAppointmentDate = appointment.date!!.split(".").joinToString("")
    getDoctorAppointmentNode(appointment.doctorID!!)
        .child(formattedAppointmentDate)
        .child(patientId)
        .push()
        .apply {
            val reference = ref
            appointment.id = reference.key
            setValue(appointment.id)
                .addOnSuccessListener {
                    getFirebaseRoot().child(MedicalAppointments.path).child(
                        patientId
                    ).child(appointment.id!!).setValue(appointment).addOnSuccessListener {
                        completionHandler(AppResult.Success(true))
                    }.addOnFailureListener {
                        completionHandler(AppResult.Error(it))
                    }
                }.addOnFailureListener {
                    completionHandler(AppResult.Error(it))
                }
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
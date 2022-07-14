package eu.ase.grupa1088.licenta.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.models.User
import eu.ase.grupa1088.licenta.repo.FirebaseEndpoints.*
import eu.ase.grupa1088.licenta.utils.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow

sealed class FirebaseEndpoints(val path: String) {
    object Users : FirebaseEndpoints("Users")
    object MedicalAppointments : FirebaseEndpoints("Appointments")
    object MedicalRecords : FirebaseEndpoints("MedicalRecords")
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
            ?: completionHandler(AppResult.Error(IllegalArgumentException("No appointments for today. See a doctor if you need assistance.")))
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
                patientSnapshot.children.onEach { dayAppointmentSnapshot ->
                    dayAppointmentSnapshot.children.onEach { appointmentDetailsSnapshot ->
                        val patientId = dayAppointmentSnapshot.key.toString()
                        val appointmentId = appointmentDetailsSnapshot.value.toString()
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
                }
            }
        } else completionHandler(AppResult.Success(MedicalAppointment(id = NO_APPOINTMENTS_FOUND)))
    }.addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }

fun deleteAppointmentFirebase(
    medicalAppointment: MedicalAppointment,
    pos: Int,
    isMarkedForDeletion: Boolean,
    isDoctor: Boolean,
    completionHandler: (AppResult<Pair<Int, Boolean>>) -> Unit
) {
    medicalAppointment.id?.let { appointmentID ->
        medicalAppointment.doctorID?.let { doctorID ->
            medicalAppointment.date?.let { appointmentDate ->
                completionHandler(AppResult.Progress)
                val formattedAppointmentDate = appointmentDate.split(".").joinToString("")
                getFirebaseRoot().child(MedicalAppointments.path).child(
                    (if (isDoctor) medicalAppointment.patientID else getCurrentUserUID())
                        ?: throwUIDException()
                ).child(appointmentID).removeValue().addOnSuccessListener {
                    getDoctorAppointmentNode(doctorID).child(formattedAppointmentDate)
                        .child(medicalAppointment.patientID ?: throwUIDException())
                        .child(appointmentID).removeValue()
                        .addOnSuccessListener {
                            completionHandler(AppResult.Success(pos to isMarkedForDeletion))
                        }.addOnFailureListener {
                            completionHandler(AppResult.Error(it))
                        }
                }.addOnFailureListener {
                    completionHandler(AppResult.Error(it))
                }
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
        }.provideFailureListener(completionHandler)
}

fun sendAppointment(
    appointment: MedicalAppointment,
    completionHandler: (AppResult<Boolean>) -> Unit
) {
    completionHandler(AppResult.Progress)
    val formattedAppointmentDate = appointment.date!!.split(".").joinToString("")
    getDoctorAppointmentNode(appointment.doctorID!!)
        .child(formattedAppointmentDate)
        .child(appointment.patientID.toString())
        .push()
        .apply {
            val reference = ref
            appointment.id = reference.key
            setValue(appointment.id)
                .addOnSuccessListener {
                    getFirebaseRoot().child(MedicalAppointments.path).child(
                        appointment.patientID.toString()
                    ).child(appointment.id!!).setValue(appointment).addOnSuccessListener {
                        completionHandler(AppResult.Success(true))
                    }.provideFailureListener(completionHandler)
                }.provideFailureListener(completionHandler)
        }
}

private fun <T, Q> Task<Q>.provideFailureListener(completionHandler: (AppResult<T>) -> Unit) {
    addOnFailureListener {
        completionHandler(AppResult.Error(it))
    }
}

fun getMedicalRecordForPatient(
    patientID: String,
) = callbackFlow<AppResult<Pair<User?, MedicalRecord>>> {
    trySendBlocking(AppResult.Progress)
    getFirebaseRoot().child(MedicalRecords.path).child(patientID).get()
        .addOnSuccessListener { medicalRecordSnapshot ->
            getMedicalRecordForPatient(medicalRecordSnapshot)?.let { medicalRecord ->
                trySendBlocking(AppResult.Success(null to medicalRecord))
            }
        }
        .addOnFailureListener {
            trySendBlocking(AppResult.Error(it))
        }
    awaitClose()
}

fun getMedicalRecordForDoctor(
    doctorID: String,
) = callbackFlow<AppResult<Pair<User?, MedicalRecord>>> {
    trySendBlocking(AppResult.Progress)
    getFirebaseRoot().child(MedicalRecords.path).get()
        .addOnSuccessListener { allPatientsRecordSnapshot ->
            if (allPatientsRecordSnapshot.hasChildren()) {
                val patientsSnapshots = allPatientsRecordSnapshot.children.filter { patientRecord ->
                    patientRecord.child("doctorID").value.toString() == doctorID
                }
                patientsSnapshots.onEach { snapshot ->
                    val patientID = snapshot.key.toString()
                    getFirebaseRoot().child(Users.path).child(patientID).get()
                        .addOnSuccessListener {
                            val patient = it.getUser()
                                ?: throw IllegalArgumentException("Error while parsing Firebase User with key ${it.key}")
                            val patientRecords = snapshot.getValue(MedicalRecord::class.java)
                                ?: throw IllegalArgumentException("Error while parsing Firebase MedicalRecord with key ${snapshot.key}")
                            trySendBlocking(AppResult.Success(patient to patientRecords))
                        }.addOnFailureListener {
                            trySend(AppResult.Error(it))
                            close(it)
                        }
                }
            } else run {
                trySendBlocking(AppResult.Error(Throwable("Hi Doc. You don't have any patients as of now.")))
                close(Throwable("Hi Doc. You don't have any patients as of now."))
            }
        }
        .addOnFailureListener {
            trySendBlocking(AppResult.Error(it))
            close(it)
        }

    awaitClose {}
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

private fun getMedicalRecordForPatient(medicalRecordSnapshot: DataSnapshot) =
    medicalRecordSnapshot.getValue(MedicalRecord::class.java)

private fun getCurrentUserUID() = FirebaseAuth.getInstance().currentUser?.uid
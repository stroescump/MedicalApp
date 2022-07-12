package eu.ase.grupa1088.licenta.utils

import com.google.firebase.database.*
import eu.ase.grupa1088.licenta.models.MedicalAppointment
import eu.ase.grupa1088.licenta.models.User

fun DatabaseReference.observeValue(completionHandler: (snapshot: AppResult<DataSnapshot>) -> Unit) {
    completionHandler(AppResult.Progress)
    addValueEventListener(provideValueEventListener(completionHandler))
}

fun DatabaseReference.observeOnce(completionHandler: (snapshot: AppResult<DataSnapshot>) -> Unit) {
    completionHandler(AppResult.Progress)
    addListenerForSingleValueEvent(provideValueEventListener(completionHandler))
}

private fun provideValueEventListener(completionHandler: (snapshot: AppResult<DataSnapshot>) -> Unit) =
    object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                completionHandler(AppResult.Success(snapshot))
            } catch (e: Throwable) {
                completionHandler(AppResult.Error(e))
            }
        }

        override fun onCancelled(error: DatabaseError) {
            completionHandler(AppResult.Error(error.toException()))
        }
    }

fun DataSnapshot.isDoctor() = if (hasChild("doctorID")) {
    child("doctorID").value.toString().isEmpty().not()
} else false

fun DataSnapshot.getDoctorId() = if (hasChild("doctorID")) {
    child("doctorID").value.toString()
} else null

fun DataSnapshot.getUser() = getValue(User::class.java)

fun DataSnapshot.getAllUsers() = getValue(object :
    GenericTypeIndicator<HashMap<String, User>>() {
})

fun DataSnapshot.getAppointments() = getValue(object :
    GenericTypeIndicator<HashMap<String, MedicalAppointment>>() {
})

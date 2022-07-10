package eu.ase.grupa1088.licenta.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

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
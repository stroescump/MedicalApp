package eu.ase.grupa1088.licenta.repo

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

fun getCurrentUserNode() = FirebaseDatabase.getInstance().reference.child("Users").child(
    FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalArgumentException("UID must be valid for user ${FirebaseAuth.getInstance().currentUser}")
)
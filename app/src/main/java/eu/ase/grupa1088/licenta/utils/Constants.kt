package eu.ase.grupa1088.licenta.utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

const val DOCTOR_ID = "DOCTOR_ID_KEY"
const val USER_KEY = "USER_KEY"
const val SHARED_PREFS_NAME = "eu.ase.grupa1088.licenta.sharedprefs"
val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
val endOfShift: LocalTime = LocalTime.of(18, 0)

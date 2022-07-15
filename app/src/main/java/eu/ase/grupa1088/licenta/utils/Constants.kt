package eu.ase.grupa1088.licenta.utils

import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

const val DOCTOR_ID = "DOCTOR_ID_KEY"
const val USER_KEY = "USER_KEY"
const val SHARED_PREFS_NAME = "eu.ase.grupa1088.licenta.sharedprefs"
const val NO_APPOINTMENTS_FOUND = "NO_APPOINTMENTS_FOUND"
const val IS_DOCTOR = "IS_DOCTOR_KEY"
const val MEDICAL_RECORD = "MEDICAL_REPORT_KEY"
const val IS_SUCCESS = "IS_SUCCESS_KEY"
const val BOTTOM_SHEET_DONE = "BOTTOM_SHEET_DONE"
val dateFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
val endOfShift: LocalTime = LocalTime.of(18, 0)

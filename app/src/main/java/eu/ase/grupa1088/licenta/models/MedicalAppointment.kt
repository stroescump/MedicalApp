package eu.ase.grupa1088.licenta.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicalAppointment(
    val name: String? = null,
    val date: String? = null,
    val startHour: String? = null,
    val endHour: String? = null
) : Parcelable

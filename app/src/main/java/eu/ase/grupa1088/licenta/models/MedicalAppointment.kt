package eu.ase.grupa1088.licenta.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Medical appointment
 *
 * @property id
 * @property doctorID
 * @property name
 * @property date
 * @property startHour
 * @property endHour
 * @property roomIDConsultation
 * @property consultationPrice
 * @property treatmentName
 * @property patientID
 * @property isSelected
 * @constructor Create empty Medical appointment
 */

@Parcelize
data class MedicalAppointment(
    var id: String? = null,
    val doctorID: String? = null,
    val name: String? = null,
    val date: String? = null,
    val startHour: String? = null,
    val endHour: String? = null,
    val roomIDConsultation: String? = null,
    val consultationPrice: Float? = 0f,
    val treatmentName: String? = null,
    val patientID: String? = null,
    var isSelected: Boolean = false
) : Parcelable

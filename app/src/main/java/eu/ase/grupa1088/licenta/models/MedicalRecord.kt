package eu.ase.grupa1088.licenta.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MedicalRecord(
    val allergiesHistory: List<String>? = listOf(),
    val diseasesHistory: List<String>? = listOf(),
    val treatmentsHistory: List<String>? = listOf(),
    var id: String? = null,
    var doctorID: String? = null,
) : Parcelable

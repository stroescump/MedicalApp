package eu.ase.grupa1088.licenta.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User
 *
 * @property nume
 * @property nrTel
 * @property email
 * @property parola
 * @property cnp
 * @property doctorID
 * @property speciality
 * @constructor Create empty User
 */

@Parcelize
data class User(
    var nume: String? = null,
    var nrTel: String? = null,
    var email: String? = null,
    var parola: String? = null,
    var cnp: String? = null,
    var doctorID: String? = null,
    val speciality: String? = null,
) : Parcelable
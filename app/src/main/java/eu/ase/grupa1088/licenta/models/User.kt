package eu.ase.grupa1088.licenta.models

data class User(
    var nume: String? = null,
    var nrTel: String? = null,
    var email: String? = null,
    var parola: String? = null,
    var cnp: String? = null,
    var doctorID: String? = null
)
package eu.ase.grupa1088.licenta.models

data class MedicalAppointment(
    val doctorName: String,
    val date: String,
    val startingHour: String,
    val endHour: String
)

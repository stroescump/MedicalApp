package eu.ase.grupa1088.licenta.utils

import eu.ase.grupa1088.licenta.models.MedicalAppointment
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

fun filterBusyDates(
    appointmentsRemoteList: List<MedicalAppointment>,
    listOfHours: MutableList<MedicalAppointment>
) {
    appointmentsRemoteList.onEach { appointmentRemote ->
        listOfHours.removeIf { it.startHour == appointmentRemote.startHour }
    }
}

fun LocalTime.roundToNearestHour(
    calendar: Calendar
): LocalTime {
    if (minute > 30) {
        calendar.add(Calendar.MINUTE, 60 - minute)
    } else if (minute in 1..29) {
        calendar.add(Calendar.MINUTE, 30 - minute)
    }
    return LocalTime.of(
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

fun getTimeNow(calendar: Calendar) = LocalTime.of(
    calendar.get(Calendar.HOUR_OF_DAY),
    calendar.get(Calendar.MINUTE)
).roundToNearestHour(calendar)

fun addHoursToAvailableSlots(
    diff: Long,
    calendar: Calendar,
    listOfHours: MutableList<MedicalAppointment>
) {
    for (i in 1..(diff) step 30) {
        val startHour = dateFormatter.format(calendar.time)
        calendar.add(Calendar.MINUTE, 30)
        val endHour = dateFormatter.format(calendar.time)
        listOfHours.add(
            MedicalAppointment(
                startHour = startHour,
                endHour = endHour
            )
        )
    }
}

fun showAvailableDates(
    appointmentsRemoteList: List<MedicalAppointment>,
    callback: (List<MedicalAppointment>) -> Unit
) {
    val calendar = Calendar.getInstance()
    val listOfHours = mutableListOf<MedicalAppointment>()

    val currentTime = getTimeNow(calendar)
    val diff = currentTime.until(endOfShift, ChronoUnit.MINUTES)

    addHoursToAvailableSlots(diff, calendar, listOfHours)
    filterBusyDates(appointmentsRemoteList, listOfHours)

    callback(listOfHours)
}
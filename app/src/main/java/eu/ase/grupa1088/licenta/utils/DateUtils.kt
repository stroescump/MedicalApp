package eu.ase.grupa1088.licenta.utils

import eu.ase.grupa1088.licenta.models.MedicalAppointment
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.*

val dateFormat_ddMMyyyy = SimpleDateFormat("dd.MM.yyyy")

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

fun getTimeFrom(calendar: Calendar) = LocalTime.of(
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
    isToday: Boolean,
    appointmentsRemoteList: List<MedicalAppointment>,
    callback: (List<MedicalAppointment>) -> Unit
) {
    val calendar = Calendar.getInstance()
    val listOfHours = mutableListOf<MedicalAppointment>()
    val currentTime = if (isToday) getTimeFrom(calendar) else getTimeFrom(
        beginningOfWorkingHours()
    )
    val diff = currentTime.until(endOfShift, ChronoUnit.MINUTES)

    addHoursToAvailableSlots(
        diff,
        if (isToday) calendar else beginningOfWorkingHours(),
        listOfHours
    )
    filterBusyDates(appointmentsRemoteList, listOfHours)

    callback(listOfHours)
}

private fun beginningOfWorkingHours() =
    Calendar.getInstance().also {
        it.set(Calendar.HOUR_OF_DAY, 9)
        it.set(Calendar.MINUTE, 0)
    }
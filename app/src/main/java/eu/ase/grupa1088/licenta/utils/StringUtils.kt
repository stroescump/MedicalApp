package eu.ase.grupa1088.licenta.utils

import java.util.*

fun String.capitalizeWord(): String = replaceFirstChar { char ->
    if (char.isLowerCase()) char.titlecase(
        Locale.getDefault()
    ) else char.toString()
}
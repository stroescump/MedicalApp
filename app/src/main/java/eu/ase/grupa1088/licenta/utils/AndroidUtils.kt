package eu.ase.grupa1088.licenta.utils

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import eu.ase.grupa1088.licenta.ui.base.BaseActivity

@Suppress("UNCHECKED_CAST")
fun <T> Fragment.getParentActivity(parentActivity: Class<T>) =
    if (parentActivity.superclass.name == BaseActivity::class.java.name) {
        (requireActivity() as T)
    } else throw IllegalArgumentException("Must be a BaseActivity child!")

fun <T> Fragment.navigateTo(
    destination: Class<T>,
    isFinishActivity: Boolean = false,
    extras: Bundle? = null
) {
    if (destination.superclass.name == BaseActivity::class.java.name) {
        requireActivity().apply {
            Intent(this, destination).also {
                extras?.let { safeExtras -> it.putExtras(safeExtras) }
                startActivity(it)
            }
            if (isFinishActivity) finish()
        }
    } else {
        throw IllegalArgumentException("Destination must be an Activity!")
    }
}
package eu.ase.grupa1088.licenta.utils

import android.R.layout
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import eu.ase.grupa1088.licenta.R
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

fun Fragment.displayInfo(message: String) =
    Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()

fun initArrayAdapter(context: Context, array: Array<String>) = ArrayAdapter(
    context,
    R.layout.layout_spinner,
    array
).also { it.setDropDownViewResource(layout.simple_spinner_dropdown_item) }
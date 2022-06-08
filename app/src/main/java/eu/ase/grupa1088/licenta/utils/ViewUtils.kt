package eu.ase.grupa1088.licenta.utils

import android.content.res.Resources
import android.graphics.Paint
import android.text.Editable
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.viewbinding.ViewBinding
import eu.ase.grupa1088.licenta.R

fun ViewBinding.getString(res: Int, args: Double): String {
    return root.context.getString(res, args)
}

fun hide(view: View) {
    view.visibility = View.GONE
}

fun show(view: View) {
    view.visibility = View.VISIBLE
}

fun AppCompatTextView.applyStrikeThrough() {
    paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
}

fun AppCompatTextView.resetPaintFlags() {
    paintFlags = Paint.ANTI_ALIAS_FLAG
}

infix fun AppCompatTextView.setFontSizeTo(fontSize: Float) {
    setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize)
}

fun AppCompatTextView.setFontFamily(resFont: Int, textStyle: Int) {
    setTypeface(
        ResourcesCompat.getFont(
            this.context,
            resFont
        ), textStyle
    )
}

fun getBitmapFromDrawable(
    resources: Resources,
    resInt: Int,
    theme: Resources.Theme
) =
    ResourcesCompat.getDrawable(resources, resInt, theme)?.toBitmap()

fun AppCompatEditText.value() = this.text.toString()

fun String.toEditable() = Editable.Factory.getInstance().newEditable(this)

fun inputValidator(viewArray: Array<Pair<AppCompatEditText, String>>): Boolean {
    viewArray.onEach {
        if (it.first.value().trim().isEmpty()) {
            it.first.error = it.second
            it.first.requestFocus()
        }
        if (it.first.id == R.id.etParola && it.first.value().length < 6) {
            it.first.error = it.first.context.getString(R.string.error_password_length)
            it.first.requestFocus()
        }
        return false
    }
    return true
}
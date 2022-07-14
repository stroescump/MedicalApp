package eu.ase.grupa1088.licenta.ui.medicalhistory

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import eu.ase.grupa1088.licenta.R

class MedicalDataArrayAdapter(context: Context, resource: Int, var items: List<String>) :
    ArrayAdapter<String>(context, resource, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(position: Int): String {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.layout_spinner, container, false)
        }
        (view?.findViewById(R.id.tvSpinnerData) as AppCompatTextView).text = getItem(position)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View? {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as CheckedTextView).text = getItem(position)
        return view
    }
}
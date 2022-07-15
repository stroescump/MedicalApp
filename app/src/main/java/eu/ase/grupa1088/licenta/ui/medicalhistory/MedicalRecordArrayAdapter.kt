import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import androidx.appcompat.widget.AppCompatTextView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.models.MedicalRecord
import eu.ase.grupa1088.licenta.models.User

class MedicalRecordArrayAdapter(
    context: Context,
    resource: Int,
    private val items: MutableList<Pair<User?, MedicalRecord>>
) :
    ArrayAdapter<Pair<User?, MedicalRecord>>(context, resource, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItem(position: Int): Pair<User?, MedicalRecord> {
        return items[position]
    }

    override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.layout_spinner, container, false)
        }
        (view?.findViewById(R.id.tvSpinnerData) as AppCompatTextView).text =
            getItem(position).first?.nume
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as CheckedTextView).text =
            getItem(position).first?.nume
        return view
    }

    fun updateData(newList: List<Pair<User?, MedicalRecord>>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun getMedicalRecordList() = items.toList()

    fun insertData(newRecord: Pair<User?, MedicalRecord>) {
        items.add(newRecord)
        notifyDataSetChanged()
    }
}
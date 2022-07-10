package eu.ase.grupa1088.licenta.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemCovidTestBinding

class CovidQuestionItem(ctx: Context, attributeSet: AttributeSet?) :
    ConstraintLayout(ctx, attributeSet) {
    private lateinit var binding: ItemCovidTestBinding
    var text: String
        get() {
            return binding.tvQuestion.text.toString()
        }
        set(value) {
            binding.tvQuestion.text = value
        }

    init {
        inflateLayout()
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.CovidQuestionItem)
        setQuestionBody(ta)
        ta.recycle()
    }

    private fun inflateLayout() {
        binding = ItemCovidTestBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setQuestionBody(ta: TypedArray) {
        binding.tvQuestion.text = ta.getText(R.styleable.CovidQuestionItem_questionText)
    }

    fun getResponse(): Int {
        return when (binding.radioGroup.checkedRadioButtonId) {
            R.id.rbYes -> 1
            R.id.rbNo -> 0
            else -> throw IllegalArgumentException("Only YES/NO is accepted.")
        }
    }
}
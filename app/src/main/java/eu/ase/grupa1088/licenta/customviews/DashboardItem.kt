package eu.ase.grupa1088.licenta.customviews

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.LinearLayoutCompat
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemDashboardBinding


class DashboardItem(ctx: Context, attributeSet: AttributeSet?) :
    LinearLayoutCompat(ctx, attributeSet) {
    private lateinit var binding: ItemDashboardBinding

    init {
        inflateLayout()
        val ta = context.obtainStyledAttributes(attributeSet, R.styleable.DashboardItem)
        setItemTitle(ta)
        setIcon(ta)
        ta.recycle()
    }

    private fun inflateLayout() {
        binding = ItemDashboardBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setItemTitle(ta: TypedArray) {
        binding.tvItemTitle.text = ta.getText(R.styleable.DashboardItem_itemText)
    }

    private fun setIcon(ta: TypedArray) {
        binding.ivItemIcon.setImageResource(
            ta.getResourceId(
                R.styleable.DashboardItem_iconRes,
                0
            )
        )
    }
}
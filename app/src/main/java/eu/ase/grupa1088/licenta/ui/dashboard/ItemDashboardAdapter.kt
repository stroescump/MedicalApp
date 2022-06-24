package eu.ase.grupa1088.licenta.ui.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.databinding.ItemDashboardBinding
import eu.ase.grupa1088.licenta.ui.dashboard.DashboardFragment.DashboardItem


class ItemDashboardAdapter(
    private val dashboardItems: MutableList<DashboardItem>,
    private val onItemClick: (item: DashboardItem) -> Unit
) :
    RecyclerView.Adapter<ItemDashboardAdapter.ItemDashboardVH>() {
    private lateinit var binding: ItemDashboardBinding

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemDashboardAdapter.ItemDashboardVH {
        binding = ItemDashboardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemDashboardVH()
    }

    override fun onBindViewHolder(holder: ItemDashboardAdapter.ItemDashboardVH, position: Int) {
        val currentItem = dashboardItems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = dashboardItems.size

    inner class ItemDashboardVH() :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("UseCompatLoadingForDrawables")
        fun bind(currentItem: DashboardItem) {
            with(binding) {
                root.setOnClickListener { onItemClick(currentItem) }
                DrawableCompat.wrap(
                    root.resources.getDrawable(
                        currentItem.resInt,
                        root.context.theme
                    )
                ).apply {
                    setTint(Color.WHITE)
                    ivItemIcon.background = this
                }
                tvItemTitle.text = currentItem.name
            }
        }
    }
}

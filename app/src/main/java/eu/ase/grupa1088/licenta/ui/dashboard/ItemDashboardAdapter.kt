package eu.ase.grupa1088.licenta.ui.dashboard

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.databinding.ItemDashboardBinding

class ItemDashboardAdapter(private val dashboardItems: MutableList<Pair<String, Int>>) :
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
        fun bind(currentItem: Pair<String, Int>) {
            with(binding) {
                ivItemIcon.apply {
                    setBackgroundResource(currentItem.second)
                    imageTintList =
                        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white));
                }
                tvItemTitle.text = currentItem.first
            }
        }
    }
}

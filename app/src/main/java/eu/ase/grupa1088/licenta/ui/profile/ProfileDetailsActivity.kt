package eu.ase.grupa1088.licenta.ui.profile

import android.os.Bundle
import eu.ase.grupa1088.licenta.databinding.ActivityProfileDetailsBinding
import eu.ase.grupa1088.licenta.ui.base.BaseActivity
import eu.ase.grupa1088.licenta.utils.viewBinding

class ProfileDetailsActivity : BaseActivity() {
    override val binding by viewBinding(ActivityProfileDetailsBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        with(binding) {
            btnBack.setOnClickListener { onBackPressed() }
        }
    }

    override fun initViews() {

    }

    override fun setupObservers() {

    }
}
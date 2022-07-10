package eu.ase.grupa1088.licenta.ui.base

import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar
import eu.ase.grupa1088.licenta.R
import eu.ase.grupa1088.licenta.utils.NetworkWatcher

abstract class BaseActivity : AppCompatActivity() {
    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java) }
    private val networkWatcher by lazy { NetworkWatcher(connectivityManager) }
    abstract val binding: ViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding.also {
                setContentView(it.root)
            }
            initViews()
            setupListeners()
            setupObservers()
        } catch (e: Throwable) {
            displayError(e.localizedMessage)
        }
    }


    private fun AlertDialog.Builder.setButton(
        buttonHandlerType: AlertDialogButton,
        buttonHandler: ((AlertDialog) -> DialogInterface.OnClickListener)?
    ) {
        if (buttonHandler.notNull()) {
            when (buttonHandlerType) {
                is AlertDialogButton.PositiveButton -> setPositiveButton(
                    getString(R.string.ok_button),
                    buttonHandler!!(this.create())
                )
                is AlertDialogButton.NegativeButton -> setNegativeButton(
                    getString(R.string.dismiss),
                    buttonHandler!!(this.create())
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val snackbar = createIndefiniteSnackbar(binding.root, getString(R.string.no_internet))
        handleNetworkWatcher(networkWatcher.isNetworkConnected(), snackbar)
        networkWatcher.observe(this) {
            handleNetworkWatcher(it, snackbar)
        }
    }

    private fun handleNetworkWatcher(isNetworkAvailable: Boolean, snackbar: Snackbar) {
        if (isNetworkAvailable) {
            snackbar.dismiss()
        } else {
            snackbar.show()
        }
    }

    override fun onPause() {
        super.onPause()
        networkWatcher.removeObservers(this)
    }

    fun displayError(message: String? = null) {
        hideProgress()
        createSnackbar(binding.root, message ?: getString(R.string.generic_error))
    }

    private fun createSnackbar(view: View, message: String) =
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

    private fun createIndefiniteSnackbar(view: View, message: String) =
        Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)

    private fun ((AlertDialog) -> DialogInterface.OnClickListener)?.notNull(): Boolean {
        return this != null
    }

    fun showProgress() {
        try {
            findViewById<View>(R.id.layoutProgress).visibility = View.VISIBLE
        } catch (e: Throwable) {
            throw Throwable("Make sure to include progress layout in your activity!")
        }
    }

    fun hideProgress() {
        try {
            findViewById<View>(R.id.layoutProgress).visibility = View.GONE
        } catch (e: Throwable) {
            throw Throwable("Make sure to include progress layout in your activity!")
        }
    }

    fun <T> navigateTo(
        destination: Class<T>,
        isFinishActivity: Boolean = false,
        extras: Bundle? = null
    ) {
        if (destination.superclass.name == BaseActivity::class.java.name) {
            Intent(this, destination).also {
                extras?.let { safeExtras -> it.putExtra("data", safeExtras) }
                startActivity(it)
            }
            if (isFinishActivity) finish()
        } else {
            throw IllegalArgumentException("Destination must be an Activity!")
        }
    }

    fun displayInfo(message: String) =
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    sealed class AlertDialogButton {
        object PositiveButton : AlertDialogButton()
        object NegativeButton : AlertDialogButton()
    }

    abstract fun setupListeners()
    abstract fun initViews()
    abstract fun setupObservers()
}
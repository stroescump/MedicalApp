package eu.ase.grupa1088.licenta.utils

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.LiveData
import androidx.lifecycle.distinctUntilChanged

class NetworkWatcher(private val connectivityManager: ConnectivityManager) :
    LiveData<Boolean>() {
    private val networkStateCallback by lazy { createNetworkCallback() }
    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                if (value != true)
                    postValue(true)
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            if (value != false)
                postValue(false)
        }
    }

    fun isNetworkConnected(): Boolean {
        return value ?: false
    }

    override fun onActive() {
        super.onActive()
        connectivityManager.registerDefaultNetworkCallback(networkStateCallback)
    }

    override fun onInactive() {
        super.onInactive()
        connectivityManager.unregisterNetworkCallback(networkStateCallback)
    }
}


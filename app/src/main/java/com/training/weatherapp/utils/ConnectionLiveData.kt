package com.training.weatherapp.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import com.training.weatherapp.constatns.Constants.ON_AVAILABLE_NETWORK
import com.training.weatherapp.constatns.Constants.ON_AVAILABLE_NETWORK_ADD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

const val TAG = "MyTagConnectionManager"

class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
    private lateinit var mNetworkCallback: ConnectivityManager.NetworkCallback
    private val mConnectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    fun getManager(): ConnectivityManager {
        return mConnectivityManager
    }

    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }

    override fun onActive() {
        mNetworkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        mConnectivityManager.registerNetworkCallback(networkRequest, mNetworkCallback)
    }

    override fun onInactive() {
        mConnectivityManager.unregisterNetworkCallback(mNetworkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            Log.d(TAG, String.format(ON_AVAILABLE_NETWORK,network))
            val networkCapabilities = mConnectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            Log.d(TAG, String.format(ON_AVAILABLE_NETWORK_ADD,network,hasInternetCapability))

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "onAvailable: adding network. $network")
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: $network")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    object DoesNetworkHaveInternet {

        fun execute(socketFactory: SocketFactory): Boolean {
            // Make sure to execute this on a background thread.
            return try {
                Log.d(TAG, "PINGING Google...")
                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                Log.d(TAG, "PING success.")
                true
            } catch (e: IOException) {
                Log.e(TAG, "No Internet Connection. $e")
                false
            }
        }
    }
}
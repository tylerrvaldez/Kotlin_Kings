package com.example.amphibians

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener

class ThirdFragment : Fragment() {
    // Initialize variables
    val btLocation: Button? = null
    val tvLatitude: TextView? = null
    val tvLongitude: TextView? = null
    var client: FusedLocationProviderClient? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize view
        val view: View = inflater.inflate(
            R.layout.fragment_third,
            container, false
        )

        // Assign variable
        val btLocation: Button = view.findViewById(R.id.bt_location)
        val tvLatitude: TextView = view.findViewById(R.id.tv_latitude)
        val tvLongitude: TextView = view.findViewById(R.id.tv_longitude)

        // Initialize location client
        client = activity?.let {
            LocationServices
                .getFusedLocationProviderClient(
                    it
                )
        }
        btLocation.setOnClickListener(
            View.OnClickListener {
                // check condition
                if (ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    // When permission is granted
                    // Call method
                    currentLocation
                } else {
                    // When permission is not granted
                    // Call method
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ),
                        100
                    )
                }
            })

        // Return view
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode, permissions, grantResults
        )
        // Check condition
        if (requestCode == 100 && grantResults.size > 0
            && (grantResults[0] + grantResults[1]
                    == PackageManager.PERMISSION_GRANTED)
        ) {
            // When permission are granted
            // Call  method
            currentLocation
        } else {
            // When permission are denied
            // Display toast
            Toast
                .makeText(
                    activity,
                    "Permission denied",
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    @get:SuppressLint("MissingPermission")
    private val currentLocation:
    // Check condition
            Unit
        private get() {
            // Initialize Location manager
            val locationManager = activity
                ?.getSystemService(
                    Context.LOCATION_SERVICE
                ) as LocationManager
            // Check condition
            if (locationManager.isProviderEnabled(
                    LocationManager.GPS_PROVIDER
                )
                || locationManager.isProviderEnabled(
                    LocationManager.NETWORK_PROVIDER
                )
            ) {
                // When location service is enabled
                // Get last location
                client?.lastLocation?.addOnCompleteListener(
                    OnCompleteListener<Location?> { task ->
                        // Initialize location
                        val location = task.result
                        // Check condition
                        if (location != null) {
                            // When location result is not
                            // null set latitude
                            tvLatitude!!.text = location
                                .latitude.toString()
                            // set longitude
                            tvLongitude!!.text = location
                                .longitude.toString()
                        } else {
                            // When location result is null
                            // initialize location request
                            val locationRequest: LocationRequest = LocationRequest()
                                .setPriority(
                                    LocationRequest.PRIORITY_HIGH_ACCURACY
                                )
                                .setInterval(10000)
                                .setFastestInterval(
                                    1000
                                )
                                .setNumUpdates(1)

                            // Initialize location call back
                            val locationCallback: LocationCallback = object : LocationCallback() {
                                override fun onLocationResult(
                                    locationResult: LocationResult
                                ) {
                                    // Initialize
                                    // location
                                    val location1: Location = locationResult
                                        .lastLocation
                                    // Set latitude
                                    tvLatitude!!.text = location1
                                        .latitude.toString()
                                    // Set longitude
                                    tvLongitude!!.text = location1
                                        .longitude.toString()
                                }
                            }

                            // Request location updates
                            client!!.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.myLooper()
                            )
                        }
                    })
            } else {
                // When location service is not enabled
                // open location setting
                startActivity(
                    Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS
                    )
                        .setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                        )
                )
            }
        }
}
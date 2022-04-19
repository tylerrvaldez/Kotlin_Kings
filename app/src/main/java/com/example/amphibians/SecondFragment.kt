package com.example.amphibians

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import org.json.JSONArray


class SecondFragment : Fragment(), OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mTrackView: View = inflater.inflate(R.layout.fragment_second, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return mTrackView

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val data = generateHeatMapData()

        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // load our weighted data
            .radius(20) // optional, in pixels, can be anything between 20 and 50
            .maxIntensity(1000.0) // set the maximum intensity
            .build()

        googleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))

        val usLatLng = LatLng(31.000000,-100.000000)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(usLatLng, 2f))
    }

    private fun generateHeatMapData(): ArrayList<WeightedLatLng> {
        val data = ArrayList<WeightedLatLng>()

        val jsonData = getJsonDataFromAsset("county.json")
        jsonData?.let {
            for (i in 0 until it.length()) {
                val entry = it.getJSONObject(i)
                val coord = entry.getJSONObject("coordinates")
                val lat = coord.getDouble("latitude")
                val lon = coord.getDouble("longitude")
                val density = entry.getDouble("confirmed")

                if (density != 0.0) {
                    val weightedLatLng = WeightedLatLng(LatLng(lat, lon), density)
                    data.add(weightedLatLng)
                }
            }
        }

        return data
    }

    private fun getJsonDataFromAsset(fileName: String): JSONArray? {
        try {
            val jsonString = assets.open(fileName).bufferedReader().use { it.readText() }
            return JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
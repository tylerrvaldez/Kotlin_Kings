package com.example.amphibians

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import com.example.amphibians.network.Amphibian
import com.example.amphibians.network.AmphibianApi
import com.example.amphibians.ui.AmphibianApiStatus
import com.example.amphibians.ui.AmphibianViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.launch
import org.json.JSONArray

class SecondFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: AmphibianViewModel by activityViewModels()

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

    private fun generateHeatMapData(): MutableList<WeightedLatLng> {
        var data :MutableList<WeightedLatLng> = mutableListOf()
        viewModel.getAmphibianList("")
//        Log.d("list","***************************************")
        val temp = viewModel.amphibians.value
//        Log.d("list", temp.toString());
        if (temp != null) {
            for (i in temp){
                if (i.coordinates.latitude != "" && i.coordinates.longitude != "") {
                    val coord = i.coordinates
                    val lat = coord.latitude.toDouble()
                    val lon = coord.longitude.toDouble()
                    val density = i.stats.confirmed.toDouble()

                    if (density != 0.0) {
                        val weightedLatLng = WeightedLatLng(LatLng(lat, lon), density)
                        data.add(weightedLatLng)
                    }
                }


            }
        }

        return data

    }
}
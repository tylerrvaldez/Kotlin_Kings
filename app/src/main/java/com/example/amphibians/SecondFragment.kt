package com.example.amphibians

import android.location.Geocoder
import android.os.Bundle
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.amphibians.network.Amphibian
import com.example.amphibians.network.AmphibianApi
import com.example.amphibians.ui.AmphibianApiStatus
import com.example.amphibians.ui.AmphibianListener
import com.example.amphibians.ui.AmphibianViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import kotlinx.coroutines.launch
import org.json.JSONArray
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener
import java.util.*

class SecondFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: AmphibianViewModel by activityViewModels()
//    var marker_1: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val mTrackView: View = inflater.inflate(R.layout.fragment_second, container, false)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return mTrackView

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val data = generateHeatMapData()
        val locations = generateMarkerData()

        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // load our weighted data
            .radius(20) // optional, in pixels, can be anything between 20 and 50
            .maxIntensity(1000.0) // set the maximum intensity
            .build()

        val markers = mutableListOf<Marker>()
        for (i in locations) {
            val marker = googleMap!!.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            i.coordinates.latitude.toDouble(),
                            i.coordinates.longitude.toDouble()
                        )
                    )
                    .title(locations.indexOf(i).toString())
                    .snippet(i.county)
            )
            markers.add(marker)

        }

        googleMap?.setOnMarkerClickListener(OnMarkerClickListener { marker -> // TODO Auto-generated method stub
            if (marker != null) {
                Log.w("Click", marker.getSnippet())
                    viewModel.onAmphibianClicked(locations[marker.getTitle().toInt()])
                    findNavController()
                        .navigate(R.id.action_secondFragment_to_amphibianDetailFragment)
                }

                return@OnMarkerClickListener true
            })

        var api_county: String = ""
        googleMap?.setOnMapLongClickListener(OnMapLongClickListener { latLng ->
            for (marker in markers) {
                if (Math.abs(marker.position.latitude - latLng.latitude) < 0.1 && Math.abs(marker.position.longitude - latLng.longitude) < 0.1) {
                    val geocoder = Geocoder(getActivity(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        latLng.latitude,
                        latLng.longitude,
                        1
                    )
                    try{
                        api_county = addresses.get(0).getSubAdminArea()
                        api_county = api_county.dropLast(7)

                        Toast.makeText(this.context, Html.fromHtml("<font color='#087f23' ><b>" + api_county + "</b></arial>"), Toast.LENGTH_SHORT)
                            .show()
                        break
                    }
                    catch(e: Exception){
                        Toast.makeText(this.context, Html.fromHtml("<font color='#087f23' ><b>" + "Preview Currently Unavailable" + "</b></arial>"), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        })


        googleMap?.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))

        val usLatLng = LatLng(31.000000, -100.000000)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(usLatLng, 5f))
    }

    private fun generateHeatMapData(): MutableList<WeightedLatLng> {
        var data: MutableList<WeightedLatLng> = mutableListOf()
        viewModel.getAmphibianList()
//        Log.d("list","***************************************")
        val temp = viewModel.amphibians.value
//        Log.d("list", temp.toString());
        if (temp != null) {
            for (i in temp) {
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

    private fun generateMarkerData(): List<Amphibian> {
        var locations: MutableList<Amphibian> = mutableListOf()
        viewModel.getAmphibianList()
        val temp = viewModel.amphibians.value
        Log.d("list", temp.toString());
        if (temp != null) {
            for (i in temp) {
                if (i.coordinates.latitude != "" && i.coordinates.longitude != "") locations.add(i)

            }
        }
        return locations

    }
}


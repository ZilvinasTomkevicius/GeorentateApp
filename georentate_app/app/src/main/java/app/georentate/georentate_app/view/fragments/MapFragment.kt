package app.georentate.georentate_app.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.graphics.Camera
import android.location.Location
import android.location.LocationManager
import android.media.Image
import android.os.Bundle
import android.view.InflateException
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import app.georentate.georentate_app.R
import app.georentate.georentate_app.db.Repository
import app.georentate.georentate_app.ux.EnableLocationListener
import app.georentate.georentate_app.viewmodel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.bottom_sheet_upper_layout.*
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*
import kotlin.collections.ArrayList

class MapFragment: Fragment() {

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var bottomSheetArrow: ImageView

    lateinit var travelButtonWalk: ImageView
    lateinit var travelButtonBike: ImageView
    lateinit var travelButtonCar: ImageView

    lateinit var distanceTravelModeIcon: ImageView
    lateinit var distanceTextView: TextView
    lateinit var timeTextView: TextView
    lateinit var checkpointAdress: TextView

    lateinit var mapFragmentLayout: CoordinatorLayout

    private var travelButtonWalkSelected: Boolean = true
    private var travelButtonBikeSelected: Boolean = false
    private var travelButtonCarSelected: Boolean = false

    private var TRAVEL_MODE = "walking"
    private var globalMarker: Marker? = null

    lateinit var travelModeChangeLoadingBar: ProgressBar

    val repository = Repository()
    val viewmodel = MapViewModel()

    /*
    UI Components
     */
    lateinit var locationButton: FloatingActionButton

    private val requestCode = 1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?  {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        requestLocationPermission()
        initUIComponents(view)
        locate()
        bottomSheetManagement()
        travelModeChooseManagement()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        val f = childFragmentManager.findFragmentById(R.id.map)

        if(f != null)
            fragmentManager!!.beginTransaction().remove(f).commit()
    }

    companion object {
        fun newInstance(): MapFragment = MapFragment()
    }

    private fun initUIComponents(view: View) {
        locationButton = view.findViewById(R.id.location_button)
        bottomSheetArrow = view.findViewById(R.id.bottom_sheet_arrow)

        travelButtonWalk = view.findViewById(R.id.directions_choose_walk)
        travelButtonBike = view.findViewById(R.id.directions_choose_bike)
        travelButtonCar = view.findViewById(R.id.directions_choose_car)

        distanceTravelModeIcon = view.findViewById(R.id.bottom_sheet_travel_mode_ImageView)
        distanceTextView = view.findViewById(R.id.bottom_sheet_distance_textView)
        timeTextView = view.findViewById(R.id.bottom_sheet_travel_time_textView)
        checkpointAdress = view.findViewById(R.id.bottom_sheet_adress_textView)

        mapFragmentLayout = view.findViewById(R.id.mapFragmentLayout)

        travelModeChangeLoadingBar = view.findViewById(R.id.travel_mode_loading_bar)
        travelModeChangeLoadingBar.visibility = View.GONE

        val bottomSheet: View = view.findViewById(R.id.bottom_sheet)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
    }

    private fun bottomSheetManagement() {
        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {

            }

            override fun onStateChanged(p0: View, p1: Int) {
                when(p1) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        bottomSheetArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        bottomSheetArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                }
            }
        })
    }

    private fun travelModeChooseManagement() {
        travelButtonWalk.setOnClickListener {
            setTravelModeUnselectedForWalk()

            if(globalMarker != null)
                getCurrentLocationAndSearchForRoute("${globalMarker!!.position.latitude},${globalMarker!!.position.longitude}")
        }
        travelButtonBike.setOnClickListener {
            setTravelModeUnselectedForBike()

            if(globalMarker != null)
                getCurrentLocationAndSearchForRoute("${globalMarker!!.position.latitude},${globalMarker!!.position.longitude}")
        }
        travelButtonCar.setOnClickListener {
            setTravelModeUnselectedForCar()

            if(globalMarker != null)
                getCurrentLocationAndSearchForRoute("${globalMarker!!.position.latitude},${globalMarker!!.position.longitude}")
        }
    }

    /*
        Sets bike and car travel mode buttons unselected
     */
    private fun setTravelModeUnselectedForWalk() {
        travelButtonWalk.setColorFilter(context!!.resources.getColor(R.color.colorAccent))
        travelButtonWalk.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_selected)
        distanceTravelModeIcon.setImageResource(R.drawable.ic_directions_walk_black_24dp)
        travelButtonWalkSelected = true

        if(travelButtonBikeSelected) {
            travelButtonBike.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonBike.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonBikeSelected = false
        }
        if(travelButtonCarSelected) {
            travelButtonCar.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonCar.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonCarSelected = false
        }
        TRAVEL_MODE = "walking"
    }

    /*
        Sets walk and car travel mode buttons unselected
     */
    private fun setTravelModeUnselectedForBike() {
        travelButtonBike.setColorFilter(context!!.resources.getColor(R.color.colorAccent))
        travelButtonBike.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_selected)
        distanceTravelModeIcon.setImageResource(R.drawable.ic_directions_bike_black_24dp)
        travelButtonBikeSelected = true

        if(travelButtonBikeSelected) {
            travelButtonWalk.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonWalk.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonWalkSelected = false
        }
        if(travelButtonCarSelected) {
            travelButtonCar.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonCar.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonCarSelected = false
        }
        TRAVEL_MODE = "bicycling"
    }

    /*
        Sets bike and walk travel mode buttons unselected
     */
    private fun setTravelModeUnselectedForCar() {
        travelButtonCar.setColorFilter(context!!.resources.getColor(R.color.colorAccent))
        travelButtonCar.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_selected)
        distanceTravelModeIcon.setImageResource(R.drawable.ic_directions_car_black_24dp)
        travelButtonCarSelected = true

        if(travelButtonWalkSelected) {
            travelButtonWalk.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonWalk.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonWalkSelected = false
        }
        if(travelButtonBikeSelected) {
            travelButtonBike.setColorFilter(context!!.resources.getColor(R.color.colorBlack))
            travelButtonBike.setBackgroundResource(R.drawable.bottom_sheet_choose_image_background_unselected)
            travelButtonBikeSelected = false
        }
        TRAVEL_MODE = "driving"
    }

    /*
    Request location permission from user
     */
    private fun requestLocationPermission() {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(activity!!, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), requestCode)
        else
            initMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            requestCode -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initMap()
                }
            }
        }
    }

    private fun initalizeMyLocation() {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap!!.isMyLocationEnabled = true
            googleMap!!.uiSettings.isMyLocationButtonEnabled = false
            googleMap!!.uiSettings.isCompassEnabled = false
        }
    }

    private fun getPermissionState(): Boolean {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            return true
        return false
    }

    /*
        initialize map
         */
    private fun initMap() {
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync {
            googleMap = it
            initalizeMyLocation()
            locateTheFirstTime()
            addMarker()

            googleMap!!.setOnMarkerClickListener(object: GoogleMap.OnMarkerClickListener {
                override fun onMarkerClick(marker: Marker?): Boolean {
                    googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(marker!!.position.latitude, marker.position.longitude), 15f))
                    globalMarker = marker
                    getCurrentLocationAndSearchForRoute("${marker.position.latitude},${marker.position.longitude}")
                    return true
                }
            })
        }
    }

    /*
    gets last location and zooms there
     */
    private fun locateTheFirstTime() {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    moveCamera(location!!)
                   // repository.addLocation(location)
                   // observeLocationUpdates()
                }
        }
    }

    private fun getCurrentLocationAndSearchForRoute(destination: String) {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    viewmodel.getGoogleMapsDirectionsSet("${location!!.latitude},${location.longitude}", destination, TRAVEL_MODE)
                    observeRouteSearchAndChanges()
                    observeProgressBar()
                }
        }
    }

    private fun locate() {
        locationButton.setOnClickListener {
            if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        animateCamera(location!!)
                       // repository.addLocation(location)
                       // observeLocationUpdates()
                    }
            }
        }
    }

    private fun moveCamera(location: Location) {
        googleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
    }

    private fun animateCamera(location: Location) {
        googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15f))
    }

    private fun observeLocationUpdates() {
        repository.locationUpdated.observe(this, androidx.lifecycle.Observer { data ->
            data?.let {
                if(it)
                    Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "Couldn't update.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /*
        observing Google API data from viewmodel
     */
    private fun observeProgressBar() {
        viewmodel.travelModeLoading.observe(this, androidx.lifecycle.Observer { data ->
            data?.let {
                travelModeChangeLoadingBar.visibility = if(it) View.VISIBLE else View.GONE
            }
        })
    }

    private fun observeRouteSearchAndChanges() {
        viewmodel.googleMapsDirectionsSet.observe(this, androidx.lifecycle.Observer { data ->
            data?.let {
                if(!it.routes.size.equals(0)) {
                    distanceTextView.text = displayExactDistance()
                    timeTextView.text = displayExactTime()
                    checkpointAdress.text = displayExactAdress()
                } else if(it.status.equals("ZERO_RESULTS")) {
                    setTravelModeUnselectedForWalk()
                    setErrorOutput("No route found.")
                } else if(it.status.equals("REQUEST_DENIED"))
                     setErrorOutput("Google service is down.")
            }
        })
    }

    private fun displayExactDistance(): String {
        if(viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].distance.value < 1000)
            return viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].distance.value.toString() + " m"
        else
            return viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].distance.text
    }

    private fun displayExactTime(): String {
        if(viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].duration.value < 60)
            return viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].duration.value.toString() + " sec"
        else
            return viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].duration.text
    }

    private fun displayExactAdress(): String {
        if(!viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].end_address.equals(""))
            return viewmodel.googleMapsDirectionsSet.value!!.routes[0].legs[0].end_address
        else
            return "Place has no exact address."
    }

    private fun setErrorOutput(message: String) {
        distanceTextView.text = message
        timeTextView.text = "-"
        checkpointAdress.text = "-"
        displayErrorMessage(message)
    }

    private fun displayErrorMessage(message: String) {
        Snackbar.make(mapFragmentLayout, message, Snackbar.LENGTH_SHORT).show()
//        val dialogBuilder = AlertDialog.Builder(context!!)
//            .setMessage(message)
//            .setNegativeButton("OK") {dialogInterface, i ->
//                dialogInterface.dismiss()
//            }
//        dialogBuilder.show()
    }

    /*
        adding demo checkpoints
     */
    private fun addMarker() {
        val latitudes = ArrayList(arrayListOf(
            54.686524,
            54.768033,
            54.770016,
            54.771384
        ))
        val longitudes = ArrayList(arrayListOf(
            25.283007,
            25.365073,
            25.346315,
            25.360767
        ))

        for(s in 0..3) {
            val markerOptions = MarkerOptions()
                .position(LatLng(latitudes[s], longitudes[s]))
                .title("first")

            googleMap!!.addMarker(markerOptions)
        }
    }
}
package app.georentate.georentate_app.view.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Camera
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.InflateException
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import app.georentate.georentate_app.R
import app.georentate.georentate_app.db.Repository
import app.georentate.georentate_app.ux.EnableLocationListener
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_map.*
import java.util.*

class MapFragment: Fragment() {

    private var mapFragment: SupportMapFragment? = null
    lateinit var googleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    lateinit var bottomSheetArrow: ImageView

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
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = false
            googleMap.uiSettings.isCompassEnabled = false
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
            getLastLocation()
        }
    }

    val repository = Repository()

    /*
    gets last location and zooms there
     */
    private fun getLastLocation() {
        if(ContextCompat.checkSelfPermission(context!!, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location!!.latitude, location.longitude), 15f))
                    repository.addLocation(location)
                    observeLocationUpdates()
                }
        }
    }

    private fun locate() {
        locationButton.setOnClickListener {
            getLastLocation()
        }
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
}
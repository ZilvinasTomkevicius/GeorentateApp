package app.georentate.georentate_app.model

import app.georentate.georentate_app.di.DaggerApiComponent
import io.reactivex.Single
import javax.inject.Inject

class GoogleMapsService {

    @Inject
    lateinit var api: GoogleMapsApi

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getGoogleMapsDirectionsSet(origin: String, destination: String, mode: String): Single<String> {
        return api.getRouteDetails(origin, destination, mode)
    }
}
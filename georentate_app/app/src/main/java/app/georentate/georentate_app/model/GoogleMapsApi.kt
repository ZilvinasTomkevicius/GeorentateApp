package app.georentate.georentate_app.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface GoogleMapsApi {

    @GET("origin={origin}&destination={destination}&mode={mode}&key=AIzaSyCLZSVZqJpseuX262cjNikvCkaxvIANUdo")
    fun getRouteDetails(@Path("origin") origin: String,
                        @Path("destination") destination: String,
                        @Path("mode")mode: String
    ): Single<String>
}
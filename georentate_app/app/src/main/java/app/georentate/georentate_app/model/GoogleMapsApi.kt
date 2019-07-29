package app.georentate.georentate_app.model

import io.reactivex.Single
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GoogleMapsApi {

    @Headers("Content-type: application/json")
    @GET("maps/api/directions/json")
    fun getRouteDetails(@Query("origin") origin: String,
                        @Query("destination") destination: String,
                        @Query("mode") mode: String,
                        @Query("key") key: String
    ): Single<GoogleMapDTO>
}
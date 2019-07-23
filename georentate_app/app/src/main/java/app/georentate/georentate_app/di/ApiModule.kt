package app.georentate.georentate_app.di

import app.georentate.georentate_app.model.GoogleMapsApi
import app.georentate.georentate_app.model.GoogleMapsService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    private val BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?"

    @Provides
    fun providesGoogleMapsApi(): GoogleMapsApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(GoogleMapsApi::class.java)
    }

    @Provides
    fun providesGoogleMapsService(): GoogleMapsService {
        return GoogleMapsService()
    }
}
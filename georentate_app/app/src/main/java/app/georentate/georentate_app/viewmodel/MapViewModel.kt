package app.georentate.georentate_app.viewmodel

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.georentate.georentate_app.R
import app.georentate.georentate_app.db.Repository
import app.georentate.georentate_app.di.DaggerApiComponent
import app.georentate.georentate_app.model.GoogleMapDTO
import app.georentate.georentate_app.model.GoogleMapsService
import app.georentate.georentate_app.model.User
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class MapViewModel: ViewModel() {

    var disposable = CompositeDisposable()
    var googleMapsDirectionsSet = MutableLiveData<GoogleMapDTO>()
    var travelModeLoading = MutableLiveData<Boolean>()

    @Inject
    lateinit var googleMapsService: GoogleMapsService

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getGoogleMapsDirectionsSet(origin: String, destination: String, mode: String) {
        travelModeLoading.value = true
        disposable.add(googleMapsService.getGoogleMapsDirectionsSet(origin, destination, mode)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<GoogleMapDTO>() {
                override fun onSuccess(value: GoogleMapDTO?) {
                    googleMapsDirectionsSet.value = value
                    travelModeLoading.value = false
                }

                override fun onError(e: Throwable?) {
                    travelModeLoading.value = false
                }
            }))
    }

    private fun convertFromJson(data: String): GoogleMapDTO {
        return Gson().fromJson(data, GoogleMapDTO::class.java)
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
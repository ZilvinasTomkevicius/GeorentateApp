package app.georentate.georentate_app.viewmodel

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import javax.inject.Inject

class MapViewModel: ViewModel() {

    var disposable = CompositeDisposable()
    var googleMapsDirectionsSet = MutableLiveData<GoogleMapDTO>()
    var googleMapsdirectionSetGot = MutableLiveData<Boolean>()

    @Inject
    lateinit var googleMapsService: GoogleMapsService

    init {
        DaggerApiComponent.create().inject(this)
    }

    fun getGoogleMapsDirectionsSet(origin: String, destination: String, mode: String) {
        disposable.add(googleMapsService.getGoogleMapsDirectionsSet(origin, destination, mode)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object: DisposableSingleObserver<String>() {
                override fun onSuccess(value: String?) {
                    googleMapsDirectionsSet.value = convertFromJson(value!!)
                    googleMapsdirectionSetGot.value = true
                }

                override fun onError(e: Throwable?) {
                    googleMapsdirectionSetGot.value = false
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
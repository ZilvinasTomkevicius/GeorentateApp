package app.georentate.georentate_app.di

import androidx.lifecycle.ViewModel
import app.georentate.georentate_app.model.GoogleMapsService
import app.georentate.georentate_app.viewmodel.MapViewModel
import dagger.Component

@Component(modules = [ApiModule::class])
interface ApiComponent {

    fun inject(service: GoogleMapsService)

    fun inject(viewModel: MapViewModel)
}
package app.georentate.georentate_app.model

import android.location.Location

data class Checkpoint(
    var id: String,
    var name: String,
    var points: String,
    var description: String,
    var latitude: Double,
    var longitude: Double)
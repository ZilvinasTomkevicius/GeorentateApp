package app.georentate.georentate_app.model

import java.util.*

data class User(
    var id: String,
    var email: String,
    var password: String,
    var date: Date,
    var points: String,
    var checkpointList: List<Checkpoint>)
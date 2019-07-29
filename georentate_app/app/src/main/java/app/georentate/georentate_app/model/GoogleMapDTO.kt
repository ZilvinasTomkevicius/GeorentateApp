package app.georentate.georentate_app.model

import com.google.gson.annotations.SerializedName

class GoogleMapDTO {
    @SerializedName("routes")
    var routes = ArrayList<Routes>()
    @SerializedName("status")
    var status = ""
}

class Routes {
    @SerializedName("legs")
    var legs = ArrayList<Legs>()
}

class Legs {
    @SerializedName("distance")
    var distance = Distance()
    @SerializedName("duration")
    var duration = Duration()
   // @SerializedName("end_adress")
    var end_address = ""
   // @SerializedName("start_adress")
    var start_address = ""
    @SerializedName("end_location")
    var end_location = Location()
    @SerializedName("start_location")
    var start_location = Location()
    @SerializedName("steps")
    var steps = ArrayList<Steps>()
}

class Steps {
    @SerializedName("distance")
    var distance = Distance()
    @SerializedName("duration")
    var duration = Duration()
    @SerializedName("end_adress")
    var end_address = ""
    @SerializedName("start_adress")
    var start_address = ""
    @SerializedName("end_location")
    var end_location = Location()
    @SerializedName("start_location")
    var start_location = Location()
    @SerializedName("polyline")
    var polyline = PolyLine()
    @SerializedName("travel_mode")
    var travel_mode = ""
    @SerializedName("maneuver")
    var maneuver = ""
}

class Duration {
    @SerializedName("text")
    var text = ""
    @SerializedName("value")
    var value = 0
}

class Distance {
    @SerializedName("text")
    var text = ""
    @SerializedName("value")
    var value = 0
}

class PolyLine {
    @SerializedName("points")
    var points = ""
}

class Location{
    @SerializedName("lat")
    var lat =""
    @SerializedName("lng")
    var lng =""
}
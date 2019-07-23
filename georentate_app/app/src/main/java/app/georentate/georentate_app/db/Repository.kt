package app.georentate.georentate_app.db

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import app.georentate.georentate_app.model.Checkpoint
import app.georentate.georentate_app.model.User
import app.georentate.georentate_app.view.MainActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore

class Repository() {

    val database = FirebaseDatabase.getInstance()
    val db = FirebaseFirestore.getInstance()
    var locationUpdated = MutableLiveData<Boolean>()

    /*
    here i m basically learning and discovering how firebase works
     */
    fun addLocation(location: Location) {
        val ref = database.getReference("Locations")

        val checkpointID = ref.push().key

        val checkpoint = Checkpoint(checkpointID!!, "pirmas", "50", "fdskgnsdkfjgnskdfg", location.latitude, location.longitude)
        ref.child(checkpointID).setValue(checkpoint).addOnSuccessListener {
            locationUpdated.value = true
        }
            .addOnFailureListener{
                locationUpdated.value = false
            }
    }
}
package app.georentate.georentate_app.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import app.georentate.georentate_app.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class SigninActivity : AppCompatActivity() {

    lateinit var providers: List<AuthUI.IdpConfig>
    val REQUEST_CODE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        initProviders()
        showSignInOptions()


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode.equals(REQUEST_CODE)) {
            val response = IdpResponse.fromResultIntent(data)

            if(resultCode.equals(Activity.RESULT_OK)) {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, user!!.uid, Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java), null)
                finish()
            } else
                Toast.makeText(this, ""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initProviders() {
        providers = Arrays.asList<AuthUI.IdpConfig>(
            AuthUI.IdpConfig.EmailBuilder().build()
        )
    }

    private fun showSignInOptions() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build(), REQUEST_CODE)
    }
}
